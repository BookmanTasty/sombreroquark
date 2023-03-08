package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.dto.CredentialsDTO;

import com.leyvadev.sombreroquark.interceptor.RefreshTokenFilter;
import com.leyvadev.sombreroquark.services.AuthService;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    @Inject
    AuthService authService;
    @Inject
    JsonWebToken jwt;


    @POST
    @Path("/login")
    public Uni<Response>  login(CredentialsDTO credentials) {
        return authService.login(credentials);
    }

    @POST
    @RefreshTokenFilter
    @Path("/token/renew")
    public Uni<Response> renewToken(@Context HttpHeaders headers) {
        return authService.renewToken(headers);
    }

    @POST
    @Path("/logout")
    public Uni<Response> logout(@Context HttpHeaders headers) {
        return Uni.createFrom().item(Response.ok().build());
    }

}
