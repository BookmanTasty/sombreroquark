package com.leyvadev.sombreroquark.interceptor;

import javax.annotation.Priority;

import com.leyvadev.sombreroquark.services.JwtService;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@RefreshTokenFilter
@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class RefreshTokenFilterImpl implements ContainerRequestFilter  {
    private static final Logger LOGGER = Logger.getLogger(RefreshTokenFilterImpl.class);
    @Inject
    JwtService jwtService;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        Cookie cookie = containerRequestContext.getCookies().get("refreshToken");
        if (cookie == null) {
            throw new IllegalArgumentException("Refresh token is not valid");
        }
        String token = cookie.getValue();
        containerRequestContext.getHeaders().add("X-Email", jwtService.verifyRefreshToken(token));
    }
}
