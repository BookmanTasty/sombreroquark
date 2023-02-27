package com.leyvadev.sombreroquark.clients;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.leyvadev.sombreroquark.dto.GoogleOAuthTokenDTO;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "google-oauth-api")
public interface GoogleOAuthClient {

    @POST
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<GoogleOAuthTokenDTO> getToken(
            @FormParam("code") String code,
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret,
            @FormParam("redirect_uri") String redirectUri,
            @FormParam("grant_type") String grantType);
}