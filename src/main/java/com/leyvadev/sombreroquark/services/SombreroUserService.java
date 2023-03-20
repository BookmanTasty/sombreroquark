package com.leyvadev.sombreroquark.services;

import com.leyvadev.sombreroquark.dto.CreateUserDTO;
import com.leyvadev.sombreroquark.dto.CredentialsDTO;
import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

public interface SombreroUserService {
    // servicio para crear un usuario
    Uni<Response> createUserWithEmailAndPassword(CreateUserDTO user, String redirect);
    Uni<SombreroUser> createUserWithOAuth(CreateUserDTO user);
    Uni<Response> verifyEmail(String token, String redirect);
    Uni<Response> getPaginatedUsers(PaginatedRequestDTO paginatedRequestDTO);
    Uni<Response> findUserByEmail(String email);
    Uni<Response> changePassword(CredentialsDTO credentialsDTO);
    Uni<Response> sendResetPassword(String email, String redirect);
    Uni<Response> verifyResetPassword(String token, String redirect);
    Uni<Response> resetPassword(HttpHeaders headers, CredentialsDTO credentialsDTO);

}
