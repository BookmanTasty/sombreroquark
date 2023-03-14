package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.dto.CreateUserDTO;
import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import com.leyvadev.sombreroquark.services.SombreroUserService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/api/users")
@Produces("application/json")
@Consumes("application/json")
public class UserResource {

    @Inject
    SombreroUserService sombreroUserService;
    @Inject
    @ConfigProperty(name = "sombreroquark.admin.group")
    String adminGroup;
    @POST
    public Uni<Response> createUserWithEmailAndPassword(CreateUserDTO user,@QueryParam("redirect") String redirect) {
        return sombreroUserService.createUserWithEmailAndPassword(user, redirect);
    }
    @GET
    @Path("/verify/email")
    public Uni<Response> verifyEmail(@QueryParam("token") String token, @QueryParam("redirect") String redirect) {
        return sombreroUserService.verifyEmail(token, redirect);
    }

    @POST
    @Authenticated
    @Path("/getPaginatedUsers")
    public Uni<Response> getPaginatedUsers(@Context SecurityContext securityContext, PaginatedRequestDTO request) {
        if (!securityContext.isUserInRole(adminGroup)) {
            return Uni.createFrom().item(Response.status(Response.Status.FORBIDDEN).build());
        }
        return sombreroUserService.getPaginatedUsers(request);
    }



}
