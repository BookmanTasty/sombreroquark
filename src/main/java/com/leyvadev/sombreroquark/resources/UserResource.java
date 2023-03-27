package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.dto.CreateUserDTO;
import com.leyvadev.sombreroquark.dto.CredentialsDTO;
import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import com.leyvadev.sombreroquark.interceptor.ResetTokenFilter;
import com.leyvadev.sombreroquark.services.SombreroUserService;
import com.leyvadev.sombreroquark.utils.Permissions;
import com.leyvadev.sombreroquark.utils.VerifyPermisionsInGroups;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Map;

@Path("/api/users")
@Produces("application/json")
@Consumes("application/json")
public class UserResource {
    private static final String EMAIL = "email";
    @Inject
    SombreroUserService sombreroUserService;
    @Inject
    VerifyPermisionsInGroups verifyPermisionsInGroups;
    @Inject
    JsonWebToken jwt;
    @POST
    @Tag(name = "Users")
    @APIResponse(
            responseCode = "200",
            description = "User created",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                            example = "{\n    \"id\": \"7883e4aa-1978-46f0-9712-c468cc7dd87c\",\n    \"username\": \"jhon\",\n    \"email\": \"doe@smail.com\",\n    \"createdAt\": \"2023-03-26T22:55:36.900710700Z\",\n    \"active\": true,\n    \"emailVerified\": false,\n    \"data\": {\n        \"note\": \"here you can enter extra data for user registration\",\n        \"name\": \"jhon\"\n    },\n    \"groups\": []\n}"
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "User already exists",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                            example = "{\n    \"error\": \"User already exists\",\n   \"code\": 400 \n}"
            )
    )
    public Uni<Response> createUserWithEmailAndPassword(CreateUserDTO user,@QueryParam("redirect") String redirect) {
        return sombreroUserService.createUserWithEmailAndPassword(user, redirect);
    }
    @GET
    @Path("/verify/email")
    @Tag(name = "Users")
    @APIResponse(
            responseCode = "302",
            description = "Redirect to url"
    )
    @APIResponse(
            responseCode = "400",
            description = "Token not valid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                            example = "{\n    \"error\": \"Token not valid\",\n   \"code\": 400 \n}"
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Link expired",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                            example = "{\n    \"error\": \"Link expired\",\n   \"code\": 400 \n}"
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "User not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                            example = "{\n    \"error\": \"User not found\",\n   \"code\": 400 \n}"
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Token already used",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                            example = "{\n    \"error\": \"Token already used\",\n   \"code\": 400 \n}"
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Redirect url not valid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                            example = "{\n    \"error\": \"Redirect url not valid\",\n   \"code\": 400 \n}"
            )
    )
    public Uni<Response> verifyEmail(@QueryParam("token") String token, @QueryParam("redirect") String redirect) {
        return sombreroUserService.verifyEmail(token, redirect);
    }

    @POST
    @Authenticated
    @Path("/getPaginatedUsers")
    @Tag(name = "Users")
    @APIResponse(
            responseCode = "200",
            description = "Paginated users",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                            example = "{\n    \"users\": [\n        {\n            \"id\": \"7883e4aa-1978-46f0-9712-c468cc7dd87c\",\n            \"username\": \"jhon\",\n            \"email\": \"doe@smail.com\",\n            \"createdAt\": \"2023-03-26T22:55:36.900710Z\",\n            \"active\": true,\n            \"emailVerified\": false,\n            \"data\": {\n                \"name\": \"jhon\",\n                \"note\": \"here you can enter extra data for user registration\"\n            },\n            \"groups\": []\n        }\n    ],\n    \"currentPage\": 1,\n    \"pageSize\": 10,\n    \"totalItems\": 3\n}"
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> getPaginatedUsers(@Context SecurityContext securityContext, PaginatedRequestDTO request) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, new String[]{Permissions.VIEW_USERS})
                .chain(() -> sombreroUserService.getPaginatedUsers(request));

    }

    @POST
    @Authenticated
    @Path("/me")
    @Tag(name = "Users")
    @APIResponse(
            responseCode = "200",
            description = "User",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                    example = "{\n    \"id\": \"7883e4aa-1978-46f0-9712-c468cc7dd87c\",\n    \"username\": \"jhon\",\n    \"email\": \"doe@smail.com\",\n    \"createdAt\": \"2023-03-26T22:55:36.900710700Z\",\n    \"active\": true,\n    \"emailVerified\": false,\n    \"data\": {\n        \"note\": \"here you can enter extra data for user registration\",\n        \"name\": \"jhon\"\n    },\n    \"groups\": []\n}"
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    public Uni<Response> getMe(@Context SecurityContext securityContext) {
        return sombreroUserService.findUserByEmail(jwt.getClaim(EMAIL));
    }

    @POST
    @Authenticated
    @Path("/findUserByEmail")
    @Tag(name = "Users")
    @APIResponse(
            responseCode = "200",
            description = "User",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                    example = "{\n    \"id\": \"7883e4aa-1978-46f0-9712-c468cc7dd87c\",\n    \"username\": \"jhon\",\n    \"email\": \"doe@smail.com\",\n    \"createdAt\": \"2023-03-26T22:55:36.900710700Z\",\n    \"active\": true,\n    \"emailVerified\": false,\n    \"data\": {\n        \"note\": \"here you can enter extra data for user registration\",\n        \"name\": \"jhon\"\n    },\n    \"groups\": []\n}"
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"User not found\",\n  \"code\": 404 \n}"
                    )
            )
    )
    public Uni<Response> findUserByEmail(@Context SecurityContext securityContext, CredentialsDTO credentialsDTO) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, new String[]{Permissions.VIEW_USERS})
                .chain(() -> sombreroUserService.findUserByEmail(credentialsDTO.getEmail()));
    }

    @POST
    @Authenticated
    @Path("/updatedata/{email}")
    @Tag(name = "Users")
    @APIResponse(
            responseCode = "200",
            description = "User",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                    example = "{\n    \"id\": \"7883e4aa-1978-46f0-9712-c468cc7dd87c\",\n    \"username\": \"jhon\",\n    \"email\": \"doe@smail.com\",\n    \"createdAt\": \"2023-03-26T22:55:36.900710700Z\",\n    \"active\": true,\n    \"emailVerified\": false,\n    \"data\": {\n        \"note\": \"here you can enter extra data for user registration\",\n        \"name\": \"jhon\"\n    },\n    \"groups\": []\n}"
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "User not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"User not found\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> updateUserData(@Context SecurityContext securityContext, Map<String,Object> data, @PathParam(EMAIL) String email) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_USER, Permissions.VIEW_USERS};
        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> sombreroUserService.updateDataUser(data, email));
    }

    @POST
    @Authenticated
    @Path("/me/changepassword")
    @Tag(name = "Users")
    @APIResponse(
            responseCode = "200",
            description = "User",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                    example = "{\n    \"id\": \"7883e4aa-1978-46f0-9712-c468cc7dd87c\",\n    \"username\": \"jhon\",\n    \"email\": \"doe@smail.com\",\n    \"createdAt\": \"2023-03-26T22:55:36.900710700Z\",\n    \"active\": true,\n    \"emailVerified\": false,\n    \"data\": {\n        \"note\": \"here you can enter extra data for user registration\",\n        \"name\": \"jhon\"\n    },\n    \"groups\": []\n}"
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    public Uni<Response> changePassword(@Context SecurityContext securityContext, CredentialsDTO credentialsDTO) {
        credentialsDTO.setEmail(jwt.getClaim(EMAIL));
        return sombreroUserService.changePassword(credentialsDTO);
    }

    @POST
    @Authenticated
    @Path("/me/updatedata")
    @Tag(name = "Users")
    @APIResponse(
            responseCode = "200",
            description = "User",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                    example = "{\n    \"id\": \"7883e4aa-1978-46f0-9712-c468cc7dd87c\",\n    \"username\": \"jhon\",\n    \"email\": \"doe@smail.com\",\n    \"createdAt\": \"2023-03-26T22:55:36.900710700Z\",\n    \"active\": true,\n    \"emailVerified\": false,\n    \"data\": {\n        \"note\": \"here you can enter extra data for user registration\",\n        \"name\": \"jhon\"\n    },\n    \"groups\": []\n}"
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    public Uni<Response> updateData(@Context SecurityContext securityContext, Map<String,Object> data) {
        return sombreroUserService.updateDataUser(data, jwt.getClaim(EMAIL));
    }

    @POST
    @Path("/reset/password")
    @Tag(name = "Users")
    @APIResponse(
            responseCode = "200",
            description = "Email sent",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                    example = "{\n    \"message\": \"Email sent\",\n    \"code\": 200\n}"
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "User not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"User not found\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Redirect url not allowed",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Redirect url not allowed\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> sendResetPasswordEmail(@QueryParam(EMAIL) String email, @QueryParam("redirect") String redirect) {
        return sombreroUserService.sendResetPassword(email, redirect);
    }
    @GET
    @Path("/reset/password")
    @Tag(name = "Users")
    @APIResponse(
            responseCode = "200",
            description = "Email sent",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                    example = "{\n    \"message\": \"Email sent\",\n    \"code\": 200\n}"
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "User not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"User not found\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Token expired",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Token expired\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse
    (
            responseCode = "400",
            description = "Token already used",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Token already used\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> resetPasswordVerify(@QueryParam("token") String token, @QueryParam("redirect") String redirect) {
        return sombreroUserService.verifyResetPassword(token, redirect);
    }

    @PUT
    @ResetTokenFilter
    @Path("/reset/password")
    @Tag(name = "Users")
    @APIResponse(
            responseCode = "200",
            description = "Password changed",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                    example = "{\n    \"message\": \"Reset password successfully\",\n    \"code\": 200\n}"
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "User not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"User not found\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> resetPassword(@Context HttpHeaders headers,CredentialsDTO credentialsDTO) {
        return sombreroUserService.resetPassword(headers,credentialsDTO);
    }
}
