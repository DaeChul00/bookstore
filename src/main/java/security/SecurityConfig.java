package security;

import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import member.model.MemberVO;
import member.repository.MemberDAO;
import order.model.CartVO;
import order.service.CartService;

@Configuration
@EnableWebSecurity
@SuppressWarnings("deprecation")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private MemberDAO memberDAO;
	
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CartService cartService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
            		.dispatcherTypeMatchers(javax.servlet.DispatcherType.FORWARD, javax.servlet.DispatcherType.ERROR).permitAll()
                    .requestMatchers(
                            AntPathRequestMatcher.antMatcher("/"),
                            AntPathRequestMatcher.antMatcher("/book/**"),
                            AntPathRequestMatcher.antMatcher("/order/cart"),
                            AntPathRequestMatcher.antMatcher("/order/nmorderlist"),
                            AntPathRequestMatcher.antMatcher("/order/addCart"),
                            AntPathRequestMatcher.antMatcher("/order/updateCartAsync"),
                            AntPathRequestMatcher.antMatcher("/order/deleteCart"),
                            AntPathRequestMatcher.antMatcher("/order/buy"),
                            AntPathRequestMatcher.antMatcher("/order/submit"),
                            AntPathRequestMatcher.antMatcher("/order/review-insert"),
                            AntPathRequestMatcher.antMatcher("/order/status"),
                            AntPathRequestMatcher.antMatcher("/order/detail"),
                            AntPathRequestMatcher.antMatcher("/cs/**"),
                            AntPathRequestMatcher.antMatcher("/signup"),
                            AntPathRequestMatcher.antMatcher("/login"),
                            AntPathRequestMatcher.antMatcher("/kakao/**"),
                            AntPathRequestMatcher.antMatcher("/email/**"),
                            AntPathRequestMatcher.antMatcher("/css/**"),
                            AntPathRequestMatcher.antMatcher("/js/**"),
                            AntPathRequestMatcher.antMatcher("/images/**")
                    ).permitAll()
                    .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**"))
                    .hasAnyRole("ADMIN", "USER_ADMIN", "BOOK_ADMIN")
                    .anyRequest().authenticated()
                )
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("memberId")
                .passwordParameter("password")
                .successHandler(customLoginSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            .and()
            // 로그아웃 시 세션 무효화 및 JSESSIONID 안전 파괴 체이닝 주입
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/book")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true);
    }

    @Bean
    public AuthenticationSuccessHandler customLoginSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                String mid = authentication.getName();
                HttpSession session = request.getSession();
                MemberVO dbMember = memberDAO.findById(mid);
                if (dbMember != null) {
                    // 카카오 로그인과 동일하게 완전한 MemberVO 객체를 세션에 저장
                    session.setAttribute("loginUser", dbMember);
                }
                Cookie[] cookies = request.getCookies();
                
                // 비회원 장바구니 쿠키가 실제 존재했는지 판단할 가드 변수
                boolean hasGuestCart = false;

                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("guestCart".equals(cookie.getName())) {
                            // 쿠키가 존재하고 비어있지 않다면 가드 가동
                            if (cookie.getValue() != null && !cookie.getValue().trim().isEmpty()) {
                                hasGuestCart = true;
                            }
                            
                            try {
                                String cartJson = URLDecoder.decode(cookie.getValue(), "UTF-8");
                                System.out.println("▶ [디버깅] 추출된 순수 쿠키 문자열: " + cartJson);
                                
                                String cleanJson = cartJson.replace("[", "").replace("]", "");
                                String[] items = cleanJson.split("\\},\\s*\\{");
                                
                                for (String item : items) {
                                    String refined = item.replace("{", "").replace("}", "");
                                    String[] sub = refined.split(",");
                                    int bookId = 0;
                                    int count = 0;

                                    for (String part : sub) {
                                        if (part.contains("bookId")) {
                                            bookId = Integer.parseInt(part.replaceAll("[^0-9]", ""));
                                        }
                                        if (part.contains("count")) {
                                            count = Integer.parseInt(part.replaceAll("[^0-9]", ""));
                                        }
                                    }

                                    if (bookId > 0 && count > 0) {
                                        CartVO cartVO = CartVO.builder().memberId(mid).bookId(bookId).count(count).build();
                                        cartService.addCart(cartVO);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            cookie.setValue("");
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }
                    }
                }
                
                if (hasGuestCart) {
                    // 비회원 상태에서 장바구니를 담고 로그인한 유저는 "장바구니 목록"으로 안내
                    System.out.println("▶ [동적 리다이렉트] 비회원 장바구니 동기화 완료 -> 장바구니 페이지로 이동");
                    response.sendRedirect(request.getContextPath() + "/order/cart");
                } else {
                    // 장바구니에 담은 게 없는 일반 로그인 유저는 설정대로 "메인 홈 화면"으로 정렬
                    System.out.println("▶ [동적 리다이렉트] 일반 로그인 수용 -> 메인 도서 페이지로 이동");
                    response.sendRedirect(request.getContextPath() + "/book");
                }
            }
        };
    }
}