package com.leyvadev.sombreroquark.utils;

import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.repositories.SombreroAllowedRedirectUrlsRepository;
import com.leyvadev.sombreroquark.repositories.SombreroUserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class UserRegistrationValidator {

    @Inject
    SombreroUserRepository userRepository;
    @Inject
    SombreroAllowedRedirectUrlsRepository redirectUrlsRepository;

    public Uni<SombreroUser> validateRegistrationData(String username, String email, String password, String redirect) {
        if (username == null || username.isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Username cannot be null or empty"));
        }
        if (email == null || email.isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Email cannot be null or empty"));
        }
        if (password == null || password.isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Password cannot be null or empty"));
        }
        if (redirect == null || redirect.isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Redirect cannot be null or empty"));
        }
        return redirectUrlsRepository.existsByUrlAndIsActive(redirect)
                .flatMap(url -> {
                   if(url == null) {
                       throw new IllegalArgumentException("Redirect URL not allowed");
                     }
                    return userRepository.findByEmail(email);
                });
    }
}