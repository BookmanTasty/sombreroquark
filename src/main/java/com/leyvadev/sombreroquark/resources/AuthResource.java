package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.dto.CredentialsDTO;

import com.leyvadev.sombreroquark.interceptor.RefreshTokenFilter;
import com.leyvadev.sombreroquark.services.AuthService;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@OpenAPIDefinition(
        info = @Info(
                title = "Authentication API",
                description = "API for user authentication and authorization",
                version = "1.0.0"
        )
)
@Tag(name = "auth", description = "Authentication operations")
public class AuthResource {
    @Inject
    AuthService authService;
    @POST
    @Path("/login")
    @Tag(name = "auth")
    @APIResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                            example = "{\n  \"accessToken\": \"token\",\n  \"tokenType\": \"Access token\"\n}"
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid user or password",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class),
                            example = "{\n  \"error\": \"Invalid user or password\",\n  \"code\": \"400\"\n}"
            )
    )
    @RequestBody(
            description = "User credentials",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = CredentialsDTO.class,
                            example = "{\n  \"email\": \"john.doe@example.com\",\n  \"password\": \"mysecretpassword\"\n}"
                    )
            )
    )
    public Uni<Response>  login(CredentialsDTO credentials) {
        return authService.login(credentials);
    }

    @POST
    @RefreshTokenFilter
    @Path("/token/renew")
    @Tag(name = "auth")
    @APIResponse(
            responseCode = "200",
            description = "Token renewed successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"accessToken\": \"token\",\n  \"tokenType\": \"Access token\"\n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid user or password",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Invalid token\",\n  \"code\": \"400\"\n}"
                    )
            )
    )
    public Uni<Response> renewToken(@Context HttpHeaders headers,@CookieParam("refreshToken") String refreshToken) {
        return authService.renewToken(headers);
    }

    @POST
    @Path("/login/magic")
    @Tag(name = "auth")
    @APIResponse(
            responseCode = "200",
            description = "Magic link sent successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\"message\":\"Email sent\",\"code\":\"200\"}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid user or password",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Invalid token\",\n  \"code\": \"400\"\n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid email or redirect URL",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            example = "{\"error\":\"Invalid email or redirect URL\",\"code\":\"400\"}"
                    )
            )
    )
    @RequestBody(
            description = "Credentials for magic link login",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = CredentialsDTO.class,
                            example = "{\"email\":\"johndoe@example.com\"}"
                    )
            )
    )
    public Uni<Response> loginMagicLink(CredentialsDTO credentials, @QueryParam("redirect") @Parameter(description = "Redirect URL after login", example = "https://example.com/dashboard") String redirect) {
        return authService.loginMagicLink(credentials, redirect);
    }

    @GET
    @Path("/login/magic")
    public Uni<Response> verifyMagicLink(@QueryParam("token") String token, @QueryParam("redirect") String redirect) {
        return authService.verifyMagicLink(token, redirect);
    }

    @POST
    @Path("/logout")
    public Uni<Response> logout(@Context HttpHeaders headers) {
        return authService.logout(headers);
    }

}
