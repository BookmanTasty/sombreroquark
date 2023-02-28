package com.leyvadev.sombreroquark.services;

import com.leyvadev.sombreroquark.dto.CreateUserDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.core.Response;

public interface SombreroUserService {
    // servicio para crear un usuario
    Uni<Response> createUserWithEmailAndPassword(CreateUserDTO user, String redirect);
    Uni<SombreroUser> createUserWithOAuth(CreateUserDTO user);

}
