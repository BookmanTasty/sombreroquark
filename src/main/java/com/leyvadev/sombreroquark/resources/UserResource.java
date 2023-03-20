package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.dto.CreateUserDTO;
import com.leyvadev.sombreroquark.dto.CredentialsDTO;
import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import com.leyvadev.sombreroquark.services.SombreroUserService;
import com.leyvadev.sombreroquark.utils.Permissions;
import com.leyvadev.sombreroquark.utils.VerifyPermisionsInGroups;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;


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
    VerifyPermisionsInGroups verifyPermisionsInGroups;
    @Inject
    JsonWebToken jwt;
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
        String[] groups = jwt.getGroups().toArray(String[]::new);
        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, new String[]{Permissions.VIEW_USERS})
                .chain(() -> sombreroUserService.getPaginatedUsers(request));

    }


    @POST
    @Authenticated
    @Path("/me")
    public Uni<Response> getMe(@Context SecurityContext securityContext) {
        return sombreroUserService.findUserByEmail(jwt.getClaim("email"));
    }

    @POST
    @Authenticated
    @Path("/findUserByEmail")
    public Uni<Response> findUserByEmail(@Context SecurityContext securityContext, CredentialsDTO credentialsDTO) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, new String[]{Permissions.VIEW_USERS})
                .chain(() -> sombreroUserService.findUserByEmail(credentialsDTO.getEmail()));
    }

    @POST
    @Authenticated
    @Path("/me/changepassword")
    public Uni<Response> changePassword(@Context SecurityContext securityContext, CredentialsDTO credentialsDTO) {
        credentialsDTO.setEmail(jwt.getClaim("email"));
        return sombreroUserService.changePassword(credentialsDTO);
    }




}
