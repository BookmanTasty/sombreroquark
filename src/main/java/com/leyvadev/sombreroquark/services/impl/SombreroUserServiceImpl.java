package com.leyvadev.sombreroquark.services.impl;

import com.leyvadev.sombreroquark.dto.CreateUserDTO;
import com.leyvadev.sombreroquark.dto.CredentialsDTO;
import com.leyvadev.sombreroquark.dto.DefaultResponseDTO;
import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.repositories.SombreroUserRepository;
import com.leyvadev.sombreroquark.services.EmailService;
import com.leyvadev.sombreroquark.services.JwtService;
import com.leyvadev.sombreroquark.services.SombreroUserService;
import com.leyvadev.sombreroquark.utils.EmailPasswordLoginValidator;
import com.leyvadev.sombreroquark.utils.PasswordHasher;
import com.leyvadev.sombreroquark.utils.UserRegistrationValidator;
import com.leyvadev.sombreroquark.utils.VerifyEmailValidator;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@ApplicationScoped
public class SombreroUserServiceImpl implements SombreroUserService {

    private static final String RESET_PASSWORD_TOKEN = "resetPasswordToken";
    private static final String PASSWORD_IS_NOT_VALID = "Password is not valid";
    @Inject
    SombreroUserRepository sombreroUserRepository;
    @Inject
    UserRegistrationValidator userRegistrationValidator;
    @Inject
    VerifyEmailValidator verifyEmailValidator;
    @Inject
    EmailPasswordLoginValidator emailPasswordLoginValidator;
    @Inject
    JwtService jwtService;
    @Inject
    EmailService emailService;

    @Override
    @Transactional
    @ReactiveTransactional
    public Uni<Response> createUserWithEmailAndPassword(CreateUserDTO user, String redirect) {
        return userRegistrationValidator.validateRegistrationData(user.getUsername(), user.getEmail(), user.getPassword(),redirect)
                .flatMap(existingUser -> {
                    if (existingUser != null) {
                        throw new IllegalArgumentException("User already exists");
                    }
                    SombreroUser newUser = new SombreroUser();
                    newUser.setUsername(user.getUsername());
                    newUser.setEmail(user.getEmail());
                    try {
                        newUser.setPassword(PasswordHasher.hashPassword(user.getPassword()));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        throw new IllegalArgumentException(PASSWORD_IS_NOT_VALID);
                    }
                    newUser.setData(user.getData());
                    return sombreroUserRepository.save(newUser).map(persistedUser -> Response.ok(persistedUser).build())
                            .onItem().invoke(response ->
                                sendEmailConfirmation((SombreroUser) response.getEntity(), redirect)
                            );
                });
    }

    @Override
    public Uni<SombreroUser> createUserWithOAuth(CreateUserDTO user) {
        SombreroUser newUser = new SombreroUser();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setEmailVerified(user.getEmailVerified());
        try {
            newUser.setPassword(PasswordHasher.hashPassword(PasswordHasher.generatePassayPassword()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalArgumentException(PASSWORD_IS_NOT_VALID);
        }
        if (user.getData() != null) {
            newUser.setData(user.getData());
        }
        return sombreroUserRepository.findByEmail(user.getEmail())
                .flatMap(existingUser -> {
                    if (existingUser != null) {
                        return Uni.createFrom().item(existingUser);
                    }
                    return sombreroUserRepository.save(newUser)
                            .onItem().invoke(this::sendWelcomeEmail);
                });
    }

    @Override
    public Uni<Response> verifyEmail(String token, String redirect) {
        return verifyEmailValidator.validateVerifyEmailData(token, redirect)
                .flatMap(user -> {
                    user.setEmailVerified(true);
                    return sombreroUserRepository.save(user).map(persistedUser -> Response.seeOther(URI.create(redirect)).build())
                            .onItem().invoke(response ->
                                sendWelcomeEmail(user)
                            );
                });
    }

    @Override
    public Uni<Response> getPaginatedUsers(PaginatedRequestDTO paginatedRequestDTO) {
        return sombreroUserRepository.getPaginatedUsers(paginatedRequestDTO)
                .map(users -> Response.ok(users).build());
    }

    @Override
    public Uni<Response> findUserByEmail(String email) {
        return sombreroUserRepository.findByEmail(email)
                .map(user -> Response.ok(user).build());
    }

    @Override
    public Uni<Response> changePassword(CredentialsDTO credentialsDTO) {
        return emailPasswordLoginValidator.validateUpdatePaswordData(credentialsDTO)
                .flatMap(user -> {
                    try {
                        user.setPassword(PasswordHasher.hashPassword(credentialsDTO.getNewPassword()));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        throw new IllegalArgumentException(PASSWORD_IS_NOT_VALID);
                    }
                    return sombreroUserRepository.save(user).map(persistedUser -> Response.ok(persistedUser).build());
                });
    }

    @Override
    public Uni<Response> sendResetPassword(String email, String redirect) {
        return verifyEmailValidator.validateMagicLinkEmailAndRedirect(email, redirect)
                .flatMap(user -> {
                    try {
                        user.setPassword(PasswordHasher.hashPassword(PasswordHasher.generatePassayPassword()));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        throw new IllegalArgumentException(PASSWORD_IS_NOT_VALID);
                    }
                    return sombreroUserRepository.save(user).map(persistedUser -> Response.ok(new DefaultResponseDTO("Email successfully sent","200")).build())
                            .onItem().invoke(response ->
                                sendPasswordResetEmail(user, redirect)
                            );
                });
    }

    @Override
    public Uni<Response> verifyResetPassword(String token, String redirect) {
        return verifyEmailValidator.validateResetPasswordToken(token, redirect)
                .flatMap(user -> {
                    LocalDateTime expiration = LocalDateTime.now().plusHours(1);
                    Cookie cookie = new Cookie(RESET_PASSWORD_TOKEN, jwtService.generatePasswordResetToken(user),"/", null);
                    NewCookie newCookie = new NewCookie(cookie,RESET_PASSWORD_TOKEN , (int) expiration.toEpochSecond(ZoneOffset.UTC), null,true,true);
                    return Uni.createFrom().item(Response.seeOther(URI.create(redirect)).cookie(newCookie).build());
                });
    }

    @Override
    public Uni<Response> resetPassword(HttpHeaders headers,CredentialsDTO credentialsDTO) {
        credentialsDTO.setEmail(headers.getHeaderString("X-Email"));
        return emailPasswordLoginValidator.validateResetPasswordData(credentialsDTO)
                .flatMap(user -> {
                    try {
                        user.setPassword(PasswordHasher.hashPassword(credentialsDTO.getNewPassword()));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        throw new IllegalArgumentException(PASSWORD_IS_NOT_VALID);
                    }
                    return sombreroUserRepository.save(user).map(persistedUser -> Response.ok(new DefaultResponseDTO("Reset password successfully","200")).build());
                });
    }

    @Override
    public Uni<Response> updateDataUser(Map<String, Object> data, String email) {
        return sombreroUserRepository.findByEmail(email)
                .flatMap(user -> {
                    if (user == null) {
                        throw new IllegalArgumentException("User not found");
                    }
                    Map<String, Object> userData = user.getDataAsMap();
                    userData.putAll(data);
                    user.setData(userData);
                    return sombreroUserRepository.save(user).map(persistedUser -> Response.ok(persistedUser).build());
                });
    }

    private void sendWelcomeEmail(SombreroUser user) {
        new Thread(() ->
            emailService.sendWelcomeEmail(user)
        ).start();
    }

    private void sendEmailConfirmation(SombreroUser user, String redirect) {
        new Thread(() ->
            emailService.sendEmailConfirmation(user, redirect)
        ).start();
    }

    private void sendPasswordResetEmail(SombreroUser user, String redirect) {
        new Thread(() ->
            emailService.sendResetPasswordEmail(user, redirect)
        ).start();
    }


}
