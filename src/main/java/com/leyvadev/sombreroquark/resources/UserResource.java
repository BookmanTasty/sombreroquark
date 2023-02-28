package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.dto.CreateUserDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.services.SombreroUserService;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/api/users")
@Produces("application/json")
@Consumes("application/json")
public class UserResource {

    @Inject
    SombreroUserService sombreroUserService;

    @POST
    public Uni<Response> createUserWithEmailAndPassword(CreateUserDTO user,@QueryParam("redirect") String redirect) {
        return sombreroUserService.createUserWithEmailAndPassword(user, redirect);
    }

    @GET
    @Path("/verify/email")
    public Uni<Response> verifyEmail(@QueryParam("token") String token, @QueryParam("redirect") String redirect) {
        //regresamos la concadenacion de token y redirect
        return Uni.createFrom().item(Response.ok(token + redirect).build());
    }

}
