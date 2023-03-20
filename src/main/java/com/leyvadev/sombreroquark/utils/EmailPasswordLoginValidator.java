package com.leyvadev.sombreroquark.utils;


import com.leyvadev.sombreroquark.dto.CredentialsDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.repositories.SombreroUserRepository;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@ApplicationScoped
public class EmailPasswordLoginValidator {
    private static final Logger LOGGER = Logger.getLogger(EmailPasswordLoginValidator.class);
    @Inject
    SombreroUserRepository userRepository;

    public Uni<SombreroUser> validateLoginData(CredentialsDTO credentials) {
        if (credentials.getEmail() == null || credentials.getEmail().isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Email cannot be null or empty"));
        }
        if (credentials.getPassword() == null || credentials.getPassword().isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Password cannot be null or empty"));
        }
        return userRepository.findByEmail(credentials.getEmail())
                .onItem().ifNull().failWith(new IllegalArgumentException("User not found"))
                .onItem().ifNotNull().transform(user -> {
                    try {
                        if (!PasswordHasher.verifyPassword(credentials.getPassword(), user.getPassword())) {
                            throw new IllegalArgumentException("Invalid user or password");
                        }
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        LOGGER.error("Error verifying password", e);
                        throw new IllegalArgumentException("Invalid user or password");
                    }
                    return user;
                });
    }

    public Uni<SombreroUser> validateUpdatePaswordData(CredentialsDTO credentials) {
        if (credentials.getNewPassword() == null || credentials.getNewPassword().isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("New password cannot be null or empty"));
        }
        return validateLoginData(credentials);
    }
}
