package com.leyvadev.sombreroquark.services.impl;

import com.leyvadev.sombreroquark.dto.AccessToken;
import com.leyvadev.sombreroquark.dto.CredentialsDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.services.AuthService;
import com.leyvadev.sombreroquark.services.JwtService;
import com.leyvadev.sombreroquark.utils.EmailPasswordLoginValidator;
import com.leyvadev.sombreroquark.utils.VerifyEmailValidator;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {
    @Inject
    EmailPasswordLoginValidator loginValidator;
    @Inject
    VerifyEmailValidator verifyEmailValidator;
    @Inject
    JwtService jwtService;

    @Override
    public Uni<Response> login(CredentialsDTO credentials) {
        return loginValidator.validateLoginData(credentials)
                .onItem().transform(user -> {
                    AccessToken token = new AccessToken(
                            jwtService.generateAccessToken(user),
                            "Access token");
                    LocalDateTime expiration = LocalDateTime.now().plusDays(7);
                    Cookie cookie = new Cookie("refreshToken", jwtService.generateRefreshToken(user), "/", null);
                    NewCookie newCookie = new NewCookie(cookie, "Refresh token", (int) expiration.toEpochSecond(ZoneOffset.UTC), null,true,true);
                    return Response.ok(token).cookie(newCookie).build();
                });
    }

    public Uni<Response> loginOAuth(SombreroUser user,String redirect) {
        LocalDateTime expiration = LocalDateTime.now().plusDays(7);
        Cookie cookie = new Cookie("refreshToken", jwtService.generateRefreshToken(user), "/", null);
        NewCookie newCookie = new NewCookie(cookie, "Refresh token", (int) expiration.toEpochSecond(ZoneOffset.UTC), null,true,true);
        return Uni.createFrom().item(Response.seeOther(java.net.URI.create(redirect)).cookie(newCookie).build());
    }

    @Override
    public Uni<Response> renewToken(HttpHeaders headers) {
        String email = headers.getHeaderString("X-Email");
        return verifyEmailValidator.validateRefresTokenEmail(email)
                .onItem().transform(user -> {
                    AccessToken token = new AccessToken(
                            jwtService.generateAccessToken(user),
                            "Access token");
                    return Response.ok(token).build();
                });
    }

    @Override
    public Uni<Response> logout(HttpHeaders headers) {
        return null;
    }
}
