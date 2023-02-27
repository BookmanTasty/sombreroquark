package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.dto.CreateUserDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.services.SombreroUserService;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/api/users")
@Produces("application/json")
@Consumes("application/json")
public class UserResource {

    @Inject
    SombreroUserService sombreroUserService;

    @POST
    public Uni<Response> createUserWithEmailAndPassword(CreateUserDTO user) {
        return sombreroUserService.createUserWithEmailAndPassword(user);
    }

}
