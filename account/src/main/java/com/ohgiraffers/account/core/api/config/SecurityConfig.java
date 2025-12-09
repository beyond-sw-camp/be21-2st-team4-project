package com.ohgiraffers.account.core.api.config;


import com.ohgiraffers.account.security.HeaderAuthenticationFilter;
import com.ohgiraffers.account.security.RestAccessDeniedHandler;
import com.ohgiraffers.account.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(restAuthenticationEntryPoint)
                                .accessDeniedHandler(restAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth ->

                        // ✅ 회원가입 / 로그인 / 토큰검증만 인증 없이 허용
                        auth.requestMatchers(HttpMethod.POST,
                                        "/api/v1/users/signUp",
                                        "/api/v1/users/signIn",
                                        "/api/v1/users/verify"
                                ).permitAll()

                                // ✅ 마이페이지, 주문조회는 USER 권한 필요
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/users/me",
                                        "/api/v1/users/me/orders"
                                ).hasRole("USER")

                                // ✅ Swagger 허용
                                .requestMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-resources/**"
                                ).permitAll()

                                // ✅ 그 외는 전부 인증 필요
                                .anyRequest().authenticated()
                )

                // ✅ Gateway 헤더 인증 필터
                .addFilterBefore(headerAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public HeaderAuthenticationFilter headerAuthenticationFilter() {
        return new HeaderAuthenticationFilter();
    }
}
