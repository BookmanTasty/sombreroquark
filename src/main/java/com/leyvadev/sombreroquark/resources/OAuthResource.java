package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.services.OauthService;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/oauth/v1")
public class OAuthResource {

    @Inject
    OauthService oauthService;
    @GET
    @Path("/authorize")
    @Tag(name = "OAuth")
    @APIResponse(
            responseCode = "302",
            description = "Redirect to oauth provider"
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid provider",
            content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = Response.class,
                    example = "{\n  \"error\": \"Invalid provider\",\n  \"code\": \"400\"\n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid redirect URL",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Invalid redirect URL\",\n  \"code\": \"400\"\n}"
                    )
            )

    )
    public Uni<Response> authorize(@QueryParam("provider") String provider,
                         @QueryParam("redirect") String redirect)
    {
        return oauthService.authorize(provider, redirect);
    }

    @GET
    @Path("/callback")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "OAuth")
    @APIResponse(
            responseCode = "302",
            description = "Redirect to redirect URL"
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid code",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Invalid code\",\n  \"code\": \"400\"\n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid state",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Invalid state\",\n  \"code\": \"400\"\n}"
                    )
            )
    )
    public Uni<Response> callback(@QueryParam("code") String code,
                                  @QueryParam("state") String state)
    {
        return oauthService.callback(code, state);
    }

    @GET
    @Path("/certs")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "OAuth")
    @APIResponse(
            responseCode = "200",
            description = "Return certs",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"keys\": [\n    {\n      \"kty\": \"RSA\",\n      \"alg\": \"RS256\",\n      \"use\": \"sig\",\n      \"kid\": \"\",\n      \"n\": \"\",\n      \"e\": \"\"\n    }\n  ]\n}"
                    )
            )
    )
    public Uni<Response> certs()
    {
        return oauthService.certs();
    }
}
