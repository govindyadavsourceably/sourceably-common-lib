package com.io.sourceably.service.security.config;

import com.io.sourceably.security.component.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private TokenExtractor tokenExtractor;

    public JwtAuthenticationFilter(RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {

        return super.requiresAuthentication(request, response) && isNotBlank(tokenExtractor.extract(request.getHeader(HttpHeaders.AUTHORIZATION)));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        String token = tokenExtractor.extract(request.getHeader(HttpHeaders.AUTHORIZATION));

        // Perform token authentication
        final Authentication authentication = getAuthenticationManager().authenticate(
                new JwtAuthentication(
                        token
                )
        );

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication result) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(result);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException exception) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        getFailureHandler().onAuthenticationFailure(request, response, exception);
    }
}
