package com.leyvadev.sombreroquark.services.impl;

import com.leyvadev.sombreroquark.dto.CreateUserDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.repositories.SombreroUserRepository;
import com.leyvadev.sombreroquark.services.SombreroUserService;
import com.leyvadev.sombreroquark.utils.PasswordHasher;
import com.leyvadev.sombreroquark.utils.UserRegistrationValidator;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@ApplicationScoped
public class SombreroUserServiceImpl implements SombreroUserService {

    @Inject
    SombreroUserRepository sombreroUserRepository;
    @Inject
    UserRegistrationValidator userRegistrationValidator;

    @Override
    @Transactional
    @ReactiveTransactional
    public Uni<Response> createUserWithEmailAndPassword(CreateUserDTO user) {
        return userRegistrationValidator.validateRegistrationData(user.getUsername(), user.getEmail(), user.getPassword())
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
                    return sombreroUserRepository.persist(newUser).map(persistedUser -> Response.ok(persistedUser).build());
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
                    return sombreroUserRepository.persist(newUser);
                });
    }
}
