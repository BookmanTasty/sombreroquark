package com.leyvadev.sombreroquark.services;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.core.Response;

public interface OauthService {
    Uni<Response> authorize(String provider, String redirect);
    Uni<Response> callback(String code, String state);
}
