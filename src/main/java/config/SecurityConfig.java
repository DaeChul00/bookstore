package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import member.service.MemberService;

@Configuration
public class SecurityConfig {

    @Autowired
    private MemberService memberService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config)
            throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http)
            throws Exception {

        http

            // csrf 비활성화
            .csrf(csrf -> csrf.disable())

            // URL 권한 설정
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/",
                        "/book",
                        "/signup",
                        "/login",
                        "/kakao/**",
                        "/css/**",
                        "/js/**",
                        "/images/**")
                .permitAll()
                //1. 회원 관리 권한 (USER_ADMIN 또는 총관리자)
                .requestMatchers("/admin/memberList", "/admin/changeRole", "/admin/deleteMember")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER_ADMIN")
                // 2. 도서 및 주문 관리 권한 (BOOK_ADMIN 또는 총관리자)
                .requestMatchers("/admin/book/**", "/admin/order/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_BOOK_ADMIN")
                // 3. 그 외 관리자 페이지가 있다면 총관리자만 접근 가능
                .requestMatchers("/admin/**")
                .hasAnyAuthority("ROLE_ADMIN")
                
                .anyRequest()
                .authenticated()
            )

            // 로그인 설정
            .formLogin(login -> login

                .loginPage("/login")

                .loginProcessingUrl("/login")

                .defaultSuccessUrl("/book", true)

                .failureUrl("/login?error=true")

                .permitAll()
            )

            // 로그아웃 설정
            .logout(logout -> logout

                .logoutUrl("/logout")

                .logoutSuccessUrl("/book")

                .invalidateHttpSession(true)

                .deleteCookies("JSESSIONID")

                .permitAll()
            );
        

        return http.build();
    }
}