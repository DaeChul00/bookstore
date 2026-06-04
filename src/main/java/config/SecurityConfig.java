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

                .requestMatchers("/admin/**")
                .hasRole("ADMIN")

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