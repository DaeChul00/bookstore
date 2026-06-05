package security;

import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import order.model.CartVO;
import order.service.CartService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CartService cartService; 

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/"),
                        AntPathRequestMatcher.antMatcher("/book/**"),
                        AntPathRequestMatcher.antMatcher("/order/cart"),
                        AntPathRequestMatcher.antMatcher("/order/addCart"),
                        AntPathRequestMatcher.antMatcher("/order/updateCartAsync"),
                        AntPathRequestMatcher.antMatcher("/signup"),
                        AntPathRequestMatcher.antMatcher("/login"),
                        AntPathRequestMatcher.antMatcher("/kakao/**"),
                        AntPathRequestMatcher.antMatcher("/css/**"),
                        AntPathRequestMatcher.antMatcher("/js/**"),
                        AntPathRequestMatcher.antMatcher("/images/**")
                ).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasRole("ADMIN")
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("memberId")
                .passwordParameter("password")
                // 💡 [강제 고정] 무조건 이 핸들러를 타도록 시큐리티 관제탑에 명시적으로 묶어버립니다!
                .successHandler(customLoginSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/book/list")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customLoginSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                
                String mid = authentication.getName();
                System.out.println("====== 🍪 [진짜 관제탑] 대철이의 쿠키 마이그레이션 진입 성공 유저: " + mid + " ======");

                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("guestCart".equals(cookie.getName())) {
                            try {
                                String cartJson = URLDecoder.decode(cookie.getValue(), "UTF-8");
                                System.out.println("▶ [디버깅] 추출된 순수 쿠키 문자열: " + cartJson);
                                
                                // 대철이의 순수 자바 문자열 슬라이싱 알고리즘 보정 버전
                                String cleanJson = cartJson.replace("[", "").replace("]", "").replace("{", "").replace("}", "");
                                
                                if (!cleanJson.trim().isEmpty()) {
                                    // 역슬래시나 인코딩 유실에 대비해 콤마 전체를 유연하게 쪼개도록 수정
                                    String[] items = cleanJson.split("},?\\s*{?"); 
                                    if(items.length == 1) {
                                        items = cleanJson.split(",");
                                    }
                                    
                                    int bookId = 0;
                                    int count = 0;
                                    
                                    for (String item : items) {
                                        // 정밀 숫자가 파싱되는지 로깅
                                        System.out.println("▶ [디버깅] 쪼개진 단일 아이템 파트: " + item);
                                        
                                        String[] subParts = item.split(",");
                                        for(String part : subParts) {
                                            if(part.contains("bookId")) {
                                                // 문자열에서 숫자만 쏙 발라내기 기법 적용
                                                bookId = Integer.parseInt(part.replaceAll("[^0-9]", ""));
                                            } else if(part.contains("count")) {
                                                count = Integer.parseInt(part.replaceAll("[^0-9]", ""));
                                            }
                                        }
                                        
                                        if (bookId > 0 && count > 0) {
                                            CartVO cartVO = CartVO.builder()
                                                                  .memberId(mid)
                                                                  .bookId(bookId)
                                                                  .count(count)
                                                                  .build();
                                            cartService.addCart(cartVO); 
                                            System.out.println("⭕ [이사 성공] DB 꽂힘 완료: 책번호 " + bookId + " | 수량 " + count);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("❌ 쿠키 연동 파싱 예외: " + e.getMessage());
                                e.printStackTrace();
                            }
                            
                            // 이사가 무사히 수행되었든 아니든 처리가 끝나면 무조건 브라우저 쿠키는 증발시킵니다.
                            cookie.setValue("");
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                            System.out.println("🧹 브라우저 guestCart 쿠키 삭제 명령 전송 완료!");
                        }
                    }
                }
                
                response.sendRedirect(request.getContextPath() + "/order/cart");
            }
        };
    }
}