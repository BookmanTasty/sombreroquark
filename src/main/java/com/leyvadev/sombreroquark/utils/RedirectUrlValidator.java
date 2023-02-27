package com.leyvadev.sombreroquark.utils;

import com.leyvadev.sombreroquark.repositories.SombreroAllowedRedirectUrlsRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class RedirectUrlValidator {
    @Inject
    SombreroAllowedRedirectUrlsRepository redirectUrlsRepository;

    public Uni<Boolean> validateRedirectUrl(String redirectUrl) {
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Redirect URL cannot be null or empty"));
        }

        return redirectUrlsRepository.existsByUrlAndIsActive(redirectUrl)
                .onItem().ifNull().failWith(new IllegalArgumentException("Redirect URL not allowed"))
                .onItem().ifNotNull().transform(url -> true);
    }
}