package com.leyvadev.sombreroquark.services.impl;

import com.leyvadev.sombreroquark.dto.CreateUserDTO;
import com.leyvadev.sombreroquark.dto.CredentialsDTO;
import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.repositories.SombreroUserRepository;
import com.leyvadev.sombreroquark.services.EmailService;
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
import javax.ws.rs.core.Response;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@ApplicationScoped
public class SombreroUserServiceImpl implements SombreroUserService {

    @Inject
    SombreroUserRepository sombreroUserRepository;
    @Inject
    UserRegistrationValidator userRegistrationValidator;
    @Inject
    VerifyEmailValidator verifyEmailValidator;
    @Inject
    EmailPasswordLoginValidator emailPasswordLoginValidator;
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
                        throw new IllegalArgumentException("Password is not valid");
                    }
                    newUser.setData(user.getData());
                    return sombreroUserRepository.save(newUser).map(persistedUser -> Response.ok(persistedUser).build())
                            .onItem().invoke(response -> {
                                sendEmailConfirmation((SombreroUser) response.getEntity(), redirect);
                            });
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
            throw new IllegalArgumentException("Password is not valid");
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
                            .onItem().invoke(response -> {
                                sendWelcomeEmail(user);
                            });
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
                        throw new IllegalArgumentException("Password is not valid");
                    }
                    return sombreroUserRepository.save(user).map(persistedUser -> Response.ok(persistedUser).build());
                });
    }

    private void sendWelcomeEmail(SombreroUser user) {
        new Thread(() -> {
            emailService.sendWelcomeEmail(user);
        }).start();
    }

    private void sendEmailConfirmation(SombreroUser user, String redirect) {
        new Thread(() -> {
            emailService.sendEmailConfirmation(user, redirect);
        }).start();
    }


}
