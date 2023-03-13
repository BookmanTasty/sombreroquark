package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.services.OauthService;
import io.smallrye.mutiny.Uni;

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
    public Uni<Response> authorize(@QueryParam("provider") String provider,
                         @QueryParam("redirect") String redirect)
    {
        return oauthService.authorize(provider, redirect);
    }

    @GET
    @Path("/callback")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> callback(@QueryParam("code") String code,
                                  @QueryParam("state") String state)
    {
        return oauthService.callback(code, state);
    }

    @GET
        @Path("/certs")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> certs(@QueryParam("provider") String provider)
    {
        return oauthService.certs();
    }
}
