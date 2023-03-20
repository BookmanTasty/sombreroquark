package com.leyvadev.sombreroquark.utils;

import com.leyvadev.sombreroquark.repositories.SombreroGroupPermissionRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class VerifyPermisionsInGroups {
    @Inject
    SombreroGroupPermissionRepository sombreroGroupPermissionRepository;

    public Uni<List<String>> verifyPermissionsInGroups(String[] groups, String[] permissions) {
        List<String> groupsList = List.of(groups);
        List<String> permissionsList = List.of(permissions);
        return sombreroGroupPermissionRepository.getMissingPermissions(groupsList, permissionsList)
                .onItem().transform(missingPermissions -> {
                    if (missingPermissions.isEmpty()) {
                        return null;
                    } else {
                        throw new IllegalArgumentException("Missing permissions");
                    }
                });
    }
}
