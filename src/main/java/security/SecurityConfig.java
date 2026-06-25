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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import order.model.CartVO;
import order.service.CartService;

@Configuration
@EnableWebSecurity
@SuppressWarnings("deprecation")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CartService cartService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {

        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http)
            throws Exception {

        http
            .csrf().disable()

            .authorizeRequests()

            .antMatchers(
                    "/",
                    "/signup",
                    "/login",
                    "/email/**",       //  이메일 인증 허용
                    "/book/**",
                    "/kakao/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/order/cart",
                    "/order/addCart",
                    "/order/updateCartAsync"
            ).permitAll()

            .antMatchers("/admin/**")
            .hasRole("ADMIN")

            .anyRequest()
            .authenticated()

            .and()

            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("memberId")
                .passwordParameter("password")
                .successHandler(customLoginSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()

            .and()

            
        
		        .logout()
		        .logoutUrl("/logout")
		        .logoutSuccessUrl("/book/list")
		        .invalidateHttpSession(true)
		        .deleteCookies("JSESSIONID")
		        .clearAuthentication(true);
        
    }

    @Bean
    public AuthenticationSuccessHandler customLoginSuccessHandler() {

        return new AuthenticationSuccessHandler() {

            @Override
            public void onAuthenticationSuccess(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Authentication authentication)
                    throws IOException, ServletException {

                String mid = authentication.getName();

                Cookie[] cookies = request.getCookies();

                if (cookies != null) {
                    for (Cookie cookie : cookies) {

                        if ("guestCart".equals(cookie.getName())) {

                            try {

                                String cartJson =
                                        URLDecoder.decode(
                                                cookie.getValue(),
                                                "UTF-8");

                                String cleanJson =
                                        cartJson.replace("[", "")
                                                .replace("]", "");

                                String[] items =
                                        cleanJson.split("\\},\\s*\\{");

                                for (String item : items) {

                                    String refined =
                                            item.replace("{", "")
                                                .replace("}", "");

                                    String[] sub =
                                            refined.split(",");

                                    int bookId = 0;
                                    int count = 0;

                                    for (String part : sub) {

                                        if (part.contains("bookId")) {
                                            bookId =
                                                    Integer.parseInt(
                                                            part.replaceAll(
                                                                    "[^0-9]",
                                                                    ""));
                                        }

                                        if (part.contains("count")) {
                                            count =
                                                    Integer.parseInt(
                                                            part.replaceAll(
                                                                    "[^0-9]",
                                                                    ""));
                                        }
                                    }

                                    if (bookId > 0 && count > 0) {

                                        CartVO cartVO =
                                                CartVO.builder()
                                                        .memberId(mid)
                                                        .bookId(bookId)
                                                        .count(count)
                                                        .build();

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

                response.sendRedirect(
                        request.getContextPath()
                                + "/order/cart");
            }
        };
    }
}