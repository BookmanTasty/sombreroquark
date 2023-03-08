package com.leyvadev.sombreroquark.services;


import com.leyvadev.sombreroquark.model.SombreroAllowedRedirectUrl;
import com.leyvadev.sombreroquark.model.SombreroGroup;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.model.SombreroUserGroup;
import com.leyvadev.sombreroquark.repositories.SombreroAllowedRedirectUrlsRepository;
import com.leyvadev.sombreroquark.repositories.SombreroGroupRepository;
import com.leyvadev.sombreroquark.repositories.SombreroUserGroupRepository;
import com.leyvadev.sombreroquark.repositories.SombreroUserRepository;
import com.leyvadev.sombreroquark.utils.PasswordHasher;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Startup
@Singleton
public class StartupService {
    private static final Logger LOGGER = Logger.getLogger(StartupService.class);

    @Inject
    SombreroGroupRepository groupRepository;
    @Inject
    SombreroUserRepository userRepository;
    @Inject
    SombreroAllowedRedirectUrlsRepository sombreroAllowedRedirectUrlsRepository;
    @Inject
    SombreroUserGroupRepository sombreroUserGroupRepository;

    @Inject
    @ConfigProperty(name = "sombreroquark.admin.username")
    String adminUsername;
    @Inject
    @ConfigProperty(name = "sombreroquark.admin.password")
    String adminPassword;
    @Inject
    @ConfigProperty(name = "sombreroquark.admin.email")
    String adminEmail;
    @Inject
    @ConfigProperty(name = "sombreroquark.public.url")
    String publicUrl;
    @Inject
    @ConfigProperty(name = "quarkus.http.root-path")
    String rootPath;

    void onStart(@Observes StartupEvent ev) {

            LOGGER.info("Starting SombreroQuark startup service");
            SombreroGroup group = groupRepository.findByName("SombreroAdmin").await().indefinitely();
            if (group == null) {
                LOGGER.info("Default group not found, creating it");
                group = createDefaultGroup().await().indefinitely();
            }
            SombreroUser user = userRepository.findByEmail(adminEmail).await().indefinitely();
            if (user == null) {
                LOGGER.info("Default user not found, creating it");
                user = createDefaultUser().await().indefinitely();
            }
            SombreroUserGroup userGroup = sombreroUserGroupRepository.findByUserAndGroup(user, group).await().indefinitely();
            if (userGroup == null) {
                LOGGER.info("Default user not found in default group, adding it");
                createDefaultUserGroup(user, group).await().indefinitely();
            }
            SombreroAllowedRedirectUrl redirectUrl = sombreroAllowedRedirectUrlsRepository.findByUrl(publicUrl + rootPath).await().indefinitely();
            if (redirectUrl == null) {
                LOGGER.info("Default redirect url not found, creating it");
                createDefaultAllowedUrl().await().indefinitely();
            }
            LOGGER.info("SombreroQuark startup service finished");

    }

    private Uni<SombreroGroup> createDefaultGroup() {
        SombreroGroup group = new SombreroGroup();
        group.setName("SombreroAdmin");
        group.setPermissionRequired(true);
        group.setData("SombreroAdmin default group");
        return groupRepository.save(group);
    }

    private Uni<SombreroUser> createDefaultUser() {
        try{
            SombreroUser user = new SombreroUser();
            user.setEmail(adminEmail);
            user.setUsername(adminUsername);
            user.setPassword(PasswordHasher.hashPassword(adminPassword));
            user.setActive(true);
            return userRepository.save(user);
        } catch (Exception e) {
            LOGGER.error("Error creating default user", e);
            return Uni.createFrom().nullItem();
        }
    }

    private Uni<SombreroUserGroup> createDefaultUserGroup(SombreroUser user, SombreroGroup group) {
        SombreroUserGroup userGroup = new SombreroUserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(group);
        return sombreroUserGroupRepository.save(userGroup);
    }

    private Uni<SombreroAllowedRedirectUrl> createDefaultAllowedUrl() {
        SombreroAllowedRedirectUrl url = new SombreroAllowedRedirectUrl();
        url.setUrl(publicUrl + rootPath);
        url.setActive(true);
        url.setCreatedAt(Instant.now());
        url.setData("Default allowed url");
        return sombreroAllowedRedirectUrlsRepository.save(url);
    }
}
