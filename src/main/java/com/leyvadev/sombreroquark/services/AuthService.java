package com.leyvadev.sombreroquark.services;

import com.leyvadev.sombreroquark.dto.CredentialsDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

public interface AuthService {
    Uni<Response> login(CredentialsDTO credentials);
    Uni<Response> renewToken(HttpHeaders headers);
    Uni<Response> logout(HttpHeaders headers);
    Uni<Response> loginOAuth(SombreroUser user, String redirect);
    Uni<Response> loginMagicLink(CredentialsDTO credentials, String redirect);
    Uni<Response> verifyMagicLink(String token, String redirect);
}
