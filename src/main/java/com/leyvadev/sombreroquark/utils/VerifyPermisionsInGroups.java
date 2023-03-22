package com.leyvadev.sombreroquark.utils;

import com.leyvadev.sombreroquark.model.SombreroGroup;
import com.leyvadev.sombreroquark.repositories.SombreroGroupPermissionRepository;
import com.leyvadev.sombreroquark.repositories.SombreroGroupRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class VerifyPermisionsInGroups {
    @Inject
    SombreroGroupPermissionRepository sombreroGroupPermissionRepository;
    @Inject
    SombreroGroupRepository sombreroGroupRepository;

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

    public Uni<List<String>> verifyPermissionsInGroupsAndPriority(String[] groups, String[] permissions, String groupUUID) {
        List<String> groupsList = List.of(groups);
        return sombreroGroupRepository.findByUUID(groupUUID)
                .onItem().transformToUni(group -> {
                    if (group == null) {
                        throw new IllegalArgumentException("Group not found");
                    }
                    return sombreroGroupRepository.getSmallestPriorityByGroupNames(groupsList)
                            .onItem().transformToUni(smallestPriority -> {
                                if (smallestPriority > group.getPriority()) {
                                    throw new IllegalArgumentException("Group priority is too low");
                                }
                                return verifyPermissionsInGroups(groups, permissions);
                            });
                });

    }


}
