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
                            AntPathRequestMatcher.antMatcher("/order/deleteCart"),
                            AntPathRequestMatcher.antMatcher("/order/review-insert"),
                            AntPathRequestMatcher.antMatcher("/cs/**"),
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
                System.out.println("====== 🍪 [폼 로그인 수신] 쿠키 마이그레이션 진입 성공 유저: " + mid + " ======");

                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("guestCart".equals(cookie.getName())) {
                            try {
                                String cartJson = URLDecoder.decode(cookie.getValue(), "UTF-8");
                                System.out.println("▶ [디버깅] 추출된 순수 쿠키 문자열: " + cartJson);
                                
                                String cleanJson = cartJson.replace("[", "").replace("]", "");
                                String[] items = cleanJson.split("\\},\\s*\\{");
                                
                                for (String item : items) {
                                    String refinedItem = item.replace("{", "").replace("}", "");
                                    String[] subParts = refinedItem.split(",");
                                    int bookId = 0;
                                    int count = 0;
                                    
                                    for(String part : subParts) {
                                        if(part.contains("bookId")) {
                                            bookId = Integer.parseInt(part.replaceAll("[^0-9]", "").trim());
                                        } else if(part.contains("count")) {
                                            count = Integer.parseInt(part.replaceAll("[^0-9]", "").trim());
                                        }
                                    }
                                    
                                    if (bookId > 0 && count > 0) {
                                        CartVO cartVO = CartVO.builder()
                                                              .memberId(mid)
                                                              .bookId(bookId)
                                                              .count(count)
                                                              .build();
                                        cartService.addCart(cartVO); 
                                        System.out.println("⭕ [폼 로그인 이사 성공] DB 꽂힘 완료: 책번호 " + bookId + " | 수량 " + count);
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("❌ 폼 로그인 쿠키 연동 파싱 예외: " + e.getMessage());
                                e.printStackTrace();
                            }
                            
                            // 처리가 완료되면 브라우저 쿠키 소멸 명령
                            cookie.setValue("");
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                            System.out.println("🧹 브라우저 guestCart 쿠키 삭제 완료!");
                        }
                    }
                }
                response.sendRedirect(request.getContextPath() + "/order/cart");
            }
        };
    }
}