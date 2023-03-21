package com.leyvadev.sombreroquark.interceptor;

import com.leyvadev.sombreroquark.services.JwtService;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@ResetTokenFilter
@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class ResetTokenFilterImpl implements ContainerRequestFilter  {

    @Inject
    JwtService jwtService;
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        Cookie cookie = containerRequestContext.getCookies().get("resetPasswordToken");
        if (cookie == null) {
            throw new IllegalArgumentException("Reset token is not valid");
        }
        String token = cookie.getValue();
        containerRequestContext.getHeaders().add("X-Email", jwtService.verifyResetPasswordToken(token));
    }
}
