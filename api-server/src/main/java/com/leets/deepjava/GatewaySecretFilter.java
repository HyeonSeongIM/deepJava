package com.leets.deepjava;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GatewaySecretFilter implements Filter {

    private static final String HEADER_NAME = "X-Gateway-Secret";

    private final String internalSecret;

    public GatewaySecretFilter(@Value("${gateway.internal-secret}") String internalSecret) {
        this.internalSecret = internalSecret;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String secret = ((HttpServletRequest) request).getHeader(HEADER_NAME);
        if (!internalSecret.equals(secret)) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(request, response);
    }
}
