package com.leyvadev.sombreroquark.services.impl;

import com.leyvadev.sombreroquark.dto.DefaultResponseDTO;
import com.leyvadev.sombreroquark.dto.GroupDTO;
import com.leyvadev.sombreroquark.model.SombreroGroup;
import com.leyvadev.sombreroquark.repositories.*;
import com.leyvadev.sombreroquark.services.SystemService;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.UUID;

@ApplicationScoped
public class SystemServiceImpl implements SystemService {
    private static final String GROUP_NOT_FOUND = "Group not found";
    @Inject
    SombreroGroupPermissionRepository sombreroGroupPermissionRepository;
    @Inject
    SombreroPermissionRepository sombreroPermissionRepository;
    @Inject
    SombreroGroupRepository sombreroGroupRepository;
    @Inject
    SombreroUserGroupRepository sombreroUserGroupRepository;
    @Inject
    SombreroUserRepository sombreroUserRepository;
    @Override
    public Uni<Response> getPermissions() {
        return sombreroPermissionRepository.findAll().list()
                .onItem().transform(permissions -> Response.ok(permissions).build());
    }

    @Override
    public Uni<Response> getGroups() {
        return sombreroGroupRepository.getGroups()
                .onItem().transform(groups -> Response.ok(groups).build());
    }

    @Override
    public Uni<Response> createGroup(GroupDTO groupDTO) {
        if (groupDTO.getName() == null || groupDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Group name is null or empty");
        }
        SombreroGroup group = new SombreroGroup();
        group.setName(groupDTO.getName());
        group.setData(groupDTO.getData());
        group.setPriority(groupDTO.getPriority());

        return sombreroGroupRepository.save(group)
                .onItem().transform(added -> Response.ok(added).build());
    }

    @Override
    public Uni<Response> addGroupPermission(String groupId, String permissionId) {
        if (groupId == null || permissionId == null) {
            throw new IllegalArgumentException("Group or permission id is null");
        }
        UUID groupUUID = null;
        UUID permissionUUID = null;
        try {
            groupUUID = UUID.fromString(groupId);
            permissionUUID = UUID.fromString(permissionId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Group or permission id is not a valid UUID");
        }
        UUID finalPermissionUUID = permissionUUID;
        UUID finalGroupUUID = groupUUID;
        return sombreroPermissionRepository.findByUUID(permissionId)
                .flatMap(permission -> {
                        if (permission == null) {
                            throw new IllegalArgumentException("Permission not found");
                        }
                        return sombreroGroupRepository.findByUUID(groupId)
                                .flatMap(group -> {
                                    if (group == null) {
                                        throw new IllegalArgumentException(GROUP_NOT_FOUND);
                                    }
                                    return sombreroGroupPermissionRepository.addPermisionToGroupByUUID(finalPermissionUUID, finalGroupUUID)
                                            .onItem().transform(added -> Response.ok(new DefaultResponseDTO("Permision added to group","200")).build());
                                });
                });
    }

    @Override
    public Uni<Response> removeGroupPermission(String groupId, String permissionId) {
        if (groupId == null || permissionId == null) {
            throw new IllegalArgumentException("Group or permission id is null");
        }
        UUID groupUUID = null;
        UUID permissionUUID = null;
        try {
            groupUUID = UUID.fromString(groupId);
            permissionUUID = UUID.fromString(permissionId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Group or permission id is not a valid UUID");
        }
        UUID finalPermissionUUID = permissionUUID;
        UUID finalGroupUUID = groupUUID;
        return sombreroPermissionRepository.findByUUID(permissionId)
                .flatMap(permission -> {
                    if (permission == null) {
                        throw new IllegalArgumentException("Permission not found");
                    }
                    return sombreroGroupRepository.findByUUID(groupId)
                            .flatMap(group -> {
                                if (group == null) {
                                    throw new IllegalArgumentException(GROUP_NOT_FOUND);
                                }
                                return sombreroGroupPermissionRepository.removePermisionToGroupByUUID(finalPermissionUUID, finalGroupUUID)
                                        .onItem().transform(added -> Response.ok(new DefaultResponseDTO("Permision removed from group","200")).build());
                            });
                });
    }

    @Override
    public Uni<Response> addUserToGroup(String userId, String groupId) {
        if (userId == null || groupId == null) {
            throw new IllegalArgumentException("User or group id is null");
        }
        UUID userUUID = null;
        UUID groupUUID = null;
        try {
            userUUID = UUID.fromString(userId);
            groupUUID = UUID.fromString(groupId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("User or group id is not a valid UUID");
        }
        UUID finalUserUUID = userUUID;
        UUID finalGroupUUID = groupUUID;
        return sombreroGroupRepository.findByUUID(groupId)
                .flatMap(group -> {
                    if (group == null) {
                        throw new IllegalArgumentException(GROUP_NOT_FOUND);
                    }
                    return sombreroUserRepository.findByUUID(userId)
                            .flatMap(user -> {
                                if (user == null) {
                                    throw new IllegalArgumentException("User not found");
                                }
                                return sombreroUserGroupRepository.addUserToGroupByUUID(finalUserUUID, finalGroupUUID)
                                        .onItem().transform(added -> Response.ok(new DefaultResponseDTO("User added to group","200")).build());
                            });
                });
    }

    @Override
    public Uni<Response> removeUserFromGroup(String userId, String groupId) {
        if (userId == null || groupId == null) {
            throw new IllegalArgumentException("User or group id is null");
        }
        UUID userUUID = null;
        UUID groupUUID = null;
        try {
            userUUID = UUID.fromString(userId);
            groupUUID = UUID.fromString(groupId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("User or group id is not a valid UUID");
        }
        UUID finalUserUUID = userUUID;
        UUID finalGroupUUID = groupUUID;
        return sombreroGroupRepository.findByUUID(groupId)
                .flatMap(group -> {
                    if (group == null) {
                        throw new IllegalArgumentException(GROUP_NOT_FOUND);
                    }
                    return sombreroUserRepository.findByUUID(userId)
                            .flatMap(user -> {
                                if (user == null) {
                                    throw new IllegalArgumentException("User not found");
                                }
                                return sombreroUserGroupRepository.removeUserFromGroupByUUID(finalUserUUID, finalGroupUUID)
                                        .onItem().transform(added -> Response.ok(new DefaultResponseDTO("User removed from group","200")).build());
                            });
                });
    }

}
