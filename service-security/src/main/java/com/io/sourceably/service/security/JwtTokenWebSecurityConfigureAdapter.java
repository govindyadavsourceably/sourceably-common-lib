package com.io.sourceably.service.security;

import com.io.sourceably.service.security.config.JwtAuthenticationEntryPoint;
import com.io.sourceably.service.security.config.JwtAuthenticationFilter;
import com.io.sourceably.service.security.config.JwtAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
//@ComponentScan(basePackages ={"com.io.sourceably.service.security"})
@EnableWebSecurity
public abstract class JwtTokenWebSecurityConfigureAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    protected JwtAuthenticationEntryPoint unauthorizedHandler;

//    @Value("${jwt.route.authentication.create}")
    protected final static String tokenRoute = "/token";

    @Bean
    public RequestMatcher jwtAuthenticationMatcher() {
        return new NegatedRequestMatcher(
                new OrRequestMatcher(
                        new AntPathRequestMatcher(tokenRoute, "POST"),
                        new AntPathRequestMatcher(tokenRoute, "GET")
                )
        );
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtAuthenticationMatcher());
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() throws Exception {
        return new JwtAuthenticationProvider();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .cors().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterAt(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(
                        HttpMethod.GET,
                        "**/swagger-resources/**",
                        "/webjars/**",
                        "/swagger-ui/**",
                        "/service/**",
                        "/v3/api-docs/**",
                        "/css/**",
                        "/fonts/**",
                        "/static/**",
                        "/image/read/**",
                        "/gl/**",
                        "/"
                ).permitAll()
                .requestMatchers(
                        EndpointRequest.toAnyEndpoint()
                ).permitAll()
                .anyRequest().authenticated();
    }
}
