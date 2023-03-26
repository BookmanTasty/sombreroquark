package com.leyvadev.sombreroquark.services.impl;

import com.leyvadev.sombreroquark.dto.AllowedRedirectUrlDto;
import com.leyvadev.sombreroquark.dto.DefaultResponseDTO;
import com.leyvadev.sombreroquark.dto.GroupDTO;
import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import com.leyvadev.sombreroquark.model.SombreroAllowedRedirectUrl;
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
    private static final String ERROR_UUID = "UUID is not valid";
    private static final String REDIRECT_URL_NOT_FOUND = "Redirect url not found";
    private static final String REDIRECT_URL_ID_NULL = "Redirect url id is null or empty";
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
    @Inject
    SombreroAllowedRedirectUrlsRepository sombreroAllowedRedirectUrlsRepository;
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
    public Uni<Response> updateGroup(GroupDTO groupDTO, String groupId) {
        return sombreroGroupRepository.findByUUID(groupId)
                .flatMap(group -> {
                    if (group == null) {
                        throw new IllegalArgumentException(GROUP_NOT_FOUND);
                    }
                    if (groupDTO.getName() != null && !groupDTO.getName().isEmpty()) {
                        group.setName(groupDTO.getName());
                    }
                    if (groupDTO.getData() != null) {
                        group.setData(groupDTO.getData());
                    }
                    if (groupDTO.getPriority() != null) {
                        group.setPriority(groupDTO.getPriority());
                    }
                    return sombreroGroupRepository.save(group)
                            .onItem().transform(updated -> Response.ok(updated).build());
                });
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
            throw new IllegalArgumentException(ERROR_UUID);
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
            throw new IllegalArgumentException(ERROR_UUID);
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
            throw new IllegalArgumentException(ERROR_UUID);
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
            throw new IllegalArgumentException(ERROR_UUID);
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

    @Override
    public Uni<Response> getRedirectUrls(PaginatedRequestDTO paginatedRequestDTO) {
        return sombreroAllowedRedirectUrlsRepository.getPaginatedUrls(paginatedRequestDTO)
                .onItem().transform(redirectUrls -> Response.ok(redirectUrls).build());
    }

    @Override
    public Uni<Response> createRedirectUrl(AllowedRedirectUrlDto allowedRedirectUrlDto) {
        if (allowedRedirectUrlDto.getUrl() == null || allowedRedirectUrlDto.getUrl().isEmpty()) {
            throw new IllegalArgumentException("Url is null or empty");
        }
        SombreroAllowedRedirectUrl url = new SombreroAllowedRedirectUrl();
        url.setUrl(allowedRedirectUrlDto.getUrl());
        url.setData(allowedRedirectUrlDto.getData());
        return sombreroAllowedRedirectUrlsRepository.save(url)
                .onItem().transform(saved -> Response.ok(saved).build());
    }

    @Override
    public Uni<Response> updateRedirectUrl(AllowedRedirectUrlDto allowedRedirectUrlDto, String redirectUrlId) {
        if (allowedRedirectUrlDto.getData() == null || allowedRedirectUrlDto.getData().isEmpty()) {
            throw new IllegalArgumentException("Data is null or empty");
        }
        if (redirectUrlId == null || redirectUrlId.isEmpty()) {
            throw new IllegalArgumentException(REDIRECT_URL_ID_NULL);
        }
        UUID uuid = null;
        try {
            uuid = UUID.fromString(redirectUrlId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(ERROR_UUID);
        }
        return sombreroAllowedRedirectUrlsRepository.findByUUID(uuid)
                .flatMap(url -> {
                    if (url == null) {
                        throw new IllegalArgumentException(REDIRECT_URL_NOT_FOUND);
                    }
                    url.setData(allowedRedirectUrlDto.getData());
                    return sombreroAllowedRedirectUrlsRepository.save(url)
                            .onItem().transform(saved -> Response.ok(new DefaultResponseDTO("Redirect url updated","200")).build());
                });
    }

    @Override
    public Uni<Response> deactivateRedirectUrl(String redirectUrlId) {
        if (redirectUrlId == null || redirectUrlId.isEmpty()) {
            throw new IllegalArgumentException(REDIRECT_URL_ID_NULL);
        }
        UUID uuid = null;
        try {
            uuid = UUID.fromString(redirectUrlId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(ERROR_UUID);
        }
        return sombreroAllowedRedirectUrlsRepository.findByUUID(uuid)
                .flatMap(url -> {
                    if (url == null) {
                        throw new IllegalArgumentException(REDIRECT_URL_NOT_FOUND);
                    }
                    url.deactivate();
                    return sombreroAllowedRedirectUrlsRepository.save(url)
                            .onItem().transform(saved -> Response.ok(new DefaultResponseDTO("Redirect url deactivated","200")).build());
                });
    }

    @Override
    public Uni<Response> activateRedirectUrl(String redirectUrlId) {
        if (redirectUrlId == null || redirectUrlId.isEmpty()) {
            throw new IllegalArgumentException(REDIRECT_URL_ID_NULL);
        }
        UUID uuid = null;
        try {
            uuid = UUID.fromString(redirectUrlId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(ERROR_UUID);
        }
        return sombreroAllowedRedirectUrlsRepository.findByUUID(uuid)
                .flatMap(url -> {
                    if (url == null) {
                        throw new IllegalArgumentException(REDIRECT_URL_NOT_FOUND);
                    }
                    url.activate();
                    return sombreroAllowedRedirectUrlsRepository.save(url)
                            .onItem().transform(saved -> Response.ok(new DefaultResponseDTO("Redirect url activated","200")).build());
                });
    }

}
