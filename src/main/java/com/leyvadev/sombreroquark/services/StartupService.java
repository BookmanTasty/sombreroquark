package com.leyvadev.sombreroquark.services;


import com.leyvadev.sombreroquark.model.*;
import com.leyvadev.sombreroquark.repositories.*;
import com.leyvadev.sombreroquark.utils.PasswordHasher;
import com.leyvadev.sombreroquark.utils.Permissions;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    SombreroPermissionRepository sombreroPermissionRepository;
    @Inject
    SombreroGroupPermissionRepository sombreroGroupPermissionRepository;

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
    @Inject
    @ConfigProperty(name = "sombreroquark.admin.group")
    String adminGroup;

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
            Boolean userGroup = sombreroUserGroupRepository.findByUserAndGroupExists(user.getId(), group.getId()).await().indefinitely();
            if (userGroup == null || !userGroup) {
                LOGGER.info("Default user not found in default group, adding it");
                createDefaultUserGroup(user, group).await().indefinitely();
            }
            SombreroAllowedRedirectUrl redirectUrl = sombreroAllowedRedirectUrlsRepository.findByUrl(publicUrl + rootPath).await().indefinitely();
            if (redirectUrl == null) {
                LOGGER.info("Default redirect url not found, creating it");
                createDefaultAllowedUrl().await().indefinitely();
            }
            List<SombreroPermission> sombreroPermissions = new ArrayList<>();
            List<String> missingPermissions = sombreroPermissionRepository.findMissingPermissions(Permissions.getPermissions()).await().indefinitely();
            if (!missingPermissions.isEmpty()) {
                LOGGER.info("Missing permissions found, creating them");
                sombreroPermissions = createDefaultPermissions(missingPermissions).await().indefinitely();
            }
            if (!sombreroPermissions.isEmpty()) {
                LOGGER.info("Adding permissions to default group");
                addPermissionsToGroup(sombreroPermissions, group).await().indefinitely();
            }
            LOGGER.info("SombreroQuark startup service finished");
    }

    private Uni<SombreroGroup> createDefaultGroup() {
        SombreroGroup group = new SombreroGroup();
        group.setName(adminGroup);
        group.setPriority(0);
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

    private Uni<Void> createDefaultUserGroup(SombreroUser user, SombreroGroup group) {
        return sombreroUserGroupRepository.addUserToGroupByUUID(user.getId(), group.getId());
    }

    private Uni<SombreroAllowedRedirectUrl> createDefaultAllowedUrl() {
        SombreroAllowedRedirectUrl url = new SombreroAllowedRedirectUrl();
        url.setUrl(publicUrl + rootPath);
        url.setActive(true);
        url.setCreatedAt(Instant.now());
        url.setData("Default allowed url");
        return sombreroAllowedRedirectUrlsRepository.save(url);
    }

    private Uni<List<SombreroPermission>> createDefaultPermissions(List<String> permissions) {
        List<SombreroPermission> sombreroPermissions = new ArrayList<>();
        for (String permission : permissions) {
            SombreroPermission sombreroPermission = new SombreroPermission();
            sombreroPermission.setName(permission);
            SombreroPermission savedPermission = sombreroPermissionRepository.save(sombreroPermission).await().indefinitely();
            sombreroPermissions.add(savedPermission);
        }
        return Uni.createFrom().item(sombreroPermissions);
    }

    private Uni<Void> addPermissionsToGroup(List<SombreroPermission> permissions, SombreroGroup group) {
        for (SombreroPermission permission : permissions) {
            sombreroGroupPermissionRepository.addPermisionToGroupByUUID(permission.getId(), group.getId()).await().indefinitely();
        }
        return Uni.createFrom().nullItem();
    }
}
