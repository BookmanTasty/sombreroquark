package com.leyvadev.sombreroquark.services.impl;


import com.leyvadev.sombreroquark.clients.GoogleOAuthClient;
import com.leyvadev.sombreroquark.dto.CreateUserDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.services.AuthService;
import com.leyvadev.sombreroquark.services.EmailService;
import com.leyvadev.sombreroquark.services.OauthService;
import com.leyvadev.sombreroquark.services.SombreroUserService;
import com.leyvadev.sombreroquark.utils.JwtUtils;
import com.leyvadev.sombreroquark.utils.RedirectUrlValidator;
import io.smallrye.mutiny.Uni;

import io.vertx.mutiny.core.Vertx;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.core.Response;
import java.util.Map;

@ApplicationScoped
public class OauthServiceImpl implements OauthService {

    private static final Logger LOGGER = Logger.getLogger(OauthServiceImpl.class);
    @ConfigProperty(name = "sombreroquark.oauth.google.url")
    String googleUrl;
    @ConfigProperty(name = "sombreroquark.oauth.google.client-id")
    String googleClientId;
    @ConfigProperty(name = "sombreroquark.oauth.google.scope")
    String googleScope;
    @ConfigProperty(name = "sombreroquark.oauth.google.redirect-uri")
    String googleRedirectUri;
    @ConfigProperty(name = "sombreroquark.oauth.google.client-secret")
    String googleClientSecret;
    private static final String RESPONSE_TYPE = "?response_type=code";


    @Inject
    RedirectUrlValidator redirectUrlValidator;
    @RestClient
    GoogleOAuthClient googleOAuthClient;
    @Inject
    JwtUtils jwtUtils;
    @Inject
    SombreroUserService sombreroUserService;
    @Inject
    AuthService authService;


    @Override
    public Uni<Response> authorize(String provider, String redirect) {
        StringBuilder url = new StringBuilder();
        String state = provider + "|" + redirect;
        String encodedState;
        try {
            encodedState = java.net.URLEncoder.encode(state, "UTF-8");
        } catch (Exception e) {
            throw new IllegalArgumentException("State not allowed");
        }
        return redirectUrlValidator.validateRedirectUrl(redirect)
                .map(isValid -> {
                    if (!isValid) {
                        throw new IllegalArgumentException("Redirect URL not allowed");
                    }

                    if (provider == null || provider.isEmpty()) {
                        throw new IllegalArgumentException("Provider cannot be null or empty");
                    }

                    if (provider.equals("google")) {

                        url.append(googleUrl)
                                .append(RESPONSE_TYPE)
                                .append("&client_id=")
                                .append(googleClientId)
                                .append("&scope=")
                                .append(googleScope)
                                .append("&redirect_uri=")
                                .append(googleRedirectUri)
                                .append("&access_type=offline")
                                .append("&state=")
                                .append(encodedState);
                        String encodedUrl = url.toString().replace(" ", "%20");
                        return Response.seeOther(java.net.URI.create(encodedUrl)).build();
                    }

                    throw new IllegalArgumentException("Provider not supported");
                });
    }

    @Override
    public Uni<Response> callback(String code, String state) {
        String decodedState;
        try {
            decodedState = java.net.URLDecoder.decode(state, "UTF-8");
        } catch (Exception e) {
            throw new IllegalArgumentException("State not allowed");
        }
        String[] stateParts = decodedState.split("\\|");
        String provider = stateParts[0];
        String redirect = stateParts[1];
        if (provider.equals("google")) {
            return googleOAuthClient.getToken(code, googleClientId, googleClientSecret, googleRedirectUri, "authorization_code")
                    .flatMap(token -> {
                        if (token == null) {
                            throw new IllegalArgumentException("Token cannot be null");
                        }
                        Map<String, Object> claims = jwtUtils.getClaimsFromTokenProvider(token.getIdToken(),provider);
                        Map<String, Object> data = new java.util.HashMap<>();
                        data.put("google", claims);
                        CreateUserDTO user = new CreateUserDTO();
                        user.setEmail((String) claims.get("email"));
                        user.setUsername((String) claims.get("email"));
                        user.setData(data);
                        user.setEmailVerified((Boolean) claims.get("email_verified"));
                        return sombreroUserService.createUserWithOAuth(user)
                                .flatMap(userCreated -> {
                                    if (userCreated == null) {
                                        throw new IllegalArgumentException("User cannot be null");
                                    }
                                    return authService.loginOAuth(userCreated, redirect);
                                });
                    });
        }
        throw new IllegalArgumentException("Provider not supported");
    }

    @Override
    public Uni<Response> certs() {
        RsaJsonWebKey rsaJsonWebKey = jwtUtils.getRsaJsonWebKey();
        String jwk = rsaJsonWebKey.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY);
        jwk = "{\"keys\":[" + jwk + "]}";
        Response.ResponseBuilder response = Response.ok(jwk);
        return Uni.createFrom().item(response.build());
    }


}
