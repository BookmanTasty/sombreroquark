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
    //static strinf for Redirect cannot be null or empty
    private static final String REDIRECT_CANNOT_BE_NULL_OR_EMPTY = "Redirect cannot be null or empty";
    // static string for User not found
    private static final String USER_NOT_FOUND = "User not found";
    // static string for Email cannot be null or empty
    private static final String EMAIL_CANNOT_BE_NULL_OR_EMPTY = "Email cannot be null or empty";
    // static string for Redirect URL not allowed
    private static final String REDIRECT_URL_NOT_ALLOWED = "Redirect URL not allowed";
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
            throw new IllegalArgumentExceptionWithTemplate(REDIRECT_CANNOT_BE_NULL_OR_EMPTY,null);
        }
        return redirectUrlsRepository.existsByUrlAndIsActive(redirect)
                .flatMap(url -> {
                    if(url == null) {
                        throw new IllegalArgumentExceptionWithTemplate(REDIRECT_URL_NOT_ALLOWED,null);
                    }
                    String email = jwtService.verifyEmailConfirmationToken(token);
                    return userRepository.findByEmail(email).flatMap(user -> {
                        if(user == null) {
                            throw new IllegalArgumentExceptionWithTemplate(USER_NOT_FOUND);
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

    public Uni<SombreroUser> validateResetPasswordToken(String token, String redirect) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentExceptionWithTemplate("Token cannot be null or empty",null);
        }
        if (redirect == null || redirect.isEmpty()) {
            throw new IllegalArgumentExceptionWithTemplate(REDIRECT_CANNOT_BE_NULL_OR_EMPTY,null);
        }
        return redirectUrlsRepository.existsByUrlAndIsActive(redirect)
                .flatMap(url -> {
                    if(url == null) {
                        throw new IllegalArgumentExceptionWithTemplate(REDIRECT_URL_NOT_ALLOWED,null);
                    }
                    String email = jwtService.verifyResetPasswordToken(token);
                    return userRepository.findByEmail(email).flatMap(user -> {
                        if(user == null) {
                            throw new IllegalArgumentExceptionWithTemplate(USER_NOT_FOUND);
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
            throw new IllegalArgumentException(EMAIL_CANNOT_BE_NULL_OR_EMPTY);
        }
        return userRepository.findByEmail(email).map(user -> {
            if(user == null) {
                throw new IllegalArgumentException(USER_NOT_FOUND);
            }
            return user;
        });
    }
    public Uni<SombreroUser> validateMagicLinkEmailAndRedirect(String email, String redirect) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException(EMAIL_CANNOT_BE_NULL_OR_EMPTY);
        }
        if (redirect == null || redirect.isEmpty()) {
            throw new IllegalArgumentException(REDIRECT_CANNOT_BE_NULL_OR_EMPTY);
        }
        return redirectUrlsRepository.existsByUrlAndIsActive(redirect)
                .flatMap(url -> {
                    if(url == null) {
                        throw new IllegalArgumentException(REDIRECT_URL_NOT_ALLOWED);
                    }
                    return userRepository.findByEmail(email);
                });
    }

    public Uni<SombreroUser> validateMagicLinkEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException(EMAIL_CANNOT_BE_NULL_OR_EMPTY);
        }
        return userRepository.findByEmail(email);
    }
}