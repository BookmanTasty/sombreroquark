package com.leyvadev.sombreroquark.utils;

import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.repositories.SombreroAllowedRedirectUrlsRepository;
import com.leyvadev.sombreroquark.repositories.SombreroUserRepository;
import com.leyvadev.sombreroquark.services.JwtService;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
@ApplicationScoped
public class VerifyEmailValidator {
    @Inject
    SombreroUserRepository userRepository;
    @Inject
    SombreroAllowedRedirectUrlsRepository redirectUrlsRepository;
    @Inject
    JwtService jwtService;

    public Uni<SombreroUser> validateVerifyEmailData(String token, String redirect) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (redirect == null || redirect.isEmpty()) {
            throw new IllegalArgumentException("Redirect cannot be null or empty");
        }
        return redirectUrlsRepository.existsByUrlAndIsActive(redirect)
                .flatMap(url -> {
                    if(url == null) {
                        throw new IllegalArgumentException("Redirect URL not allowed");
                    }
                    String email = jwtService.verifyEmailConfirmationToken(token);
                    return userRepository.findByEmail(email);
                });
    }
}
