package com.leyvadev.sombreroquark.utils;

import com.leyvadev.sombreroquark.exceptionmappers.IllegalArgumentExceptionWithTemplate;
import com.leyvadev.sombreroquark.model.SombreroBlacklistToken;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.repositories.SombreroAllowedRedirectUrlsRepository;
import com.leyvadev.sombreroquark.repositories.SombreroBlacklistTokenRepository;
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
    SombreroBlacklistTokenRepository blacklistTokenRepository;
    @Inject
    JwtService jwtService;

    public Uni<SombreroUser> validateVerifyEmailData(String token, String redirect) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentExceptionWithTemplate("Token cannot be null or empty",null);
        }
        if (redirect == null || redirect.isEmpty()) {
            throw new IllegalArgumentExceptionWithTemplate("Redirect cannot be null or empty",null);
        }
        return redirectUrlsRepository.existsByUrlAndIsActive(redirect)
                .flatMap(url -> {
                    if(url == null) {
                        throw new IllegalArgumentExceptionWithTemplate("Redirect URL not allowed",null);
                    }
                    String email = jwtService.verifyEmailConfirmationToken(token);
                    return userRepository.findByEmail(email).flatMap(user -> {
                        if(user == null) {
                            throw new IllegalArgumentExceptionWithTemplate("User not found");
                        }
                        return blacklistTokenRepository.findByToken(token).flatMap(blacklistToken -> {
                            if(blacklistToken != null) {
                                throw new IllegalArgumentExceptionWithTemplate("Link already used", null);                            }
                            SombreroBlacklistToken blacklistTokenToSave = new SombreroBlacklistToken();
                            blacklistTokenToSave.setToken(token);
                            return blacklistTokenRepository.save(blacklistTokenToSave).replaceWith(user);
                        });
                    });
                });
    }

    public Uni<SombreroUser> validateRefresTokenEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.findByEmail(email).map(user -> {
            if(user == null) {
                throw new IllegalArgumentException("User not found");
            }
            return user;
        });
    }
    public Uni<SombreroUser> validateMagicLinkEmailAndRedirect(String email, String redirect) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (redirect == null || redirect.isEmpty()) {
            throw new IllegalArgumentException("Redirect cannot be null or empty");
        }
        return redirectUrlsRepository.existsByUrlAndIsActive(redirect)
                .flatMap(url -> {
                    if(url == null) {
                        throw new IllegalArgumentException("Redirect URL not allowed");
                    }
                    return userRepository.findByEmail(email);
                });
    }

    public Uni<SombreroUser> validateMagicLinkEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.findByEmail(email);
    }
}