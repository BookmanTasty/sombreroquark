package com.leyvadev.sombreroquark.services;

import com.leyvadev.sombreroquark.dto.AllowedRedirectUrlDto;
import com.leyvadev.sombreroquark.dto.GroupDTO;
import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.core.Response;

public interface SystemService {
    Uni<Response> getPermissions();
    Uni<Response> getGroups();
    Uni<Response> createGroup(GroupDTO groupDTO);
    Uni<Response> updateGroup(GroupDTO groupDTO, String groupId);
    Uni<Response> addGroupPermission(String groupId, String permissionId);
    Uni<Response> removeGroupPermission(String groupId, String permissionId);
    Uni<Response> addUserToGroup(String userId, String groupId);
    Uni<Response> removeUserFromGroup(String userId, String groupId);
    Uni<Response> getRedirectUrls(PaginatedRequestDTO paginatedRequestDTO);
    Uni<Response> createRedirectUrl(AllowedRedirectUrlDto allowedRedirectUrlDto);
    Uni<Response> updateRedirectUrl(AllowedRedirectUrlDto allowedRedirectUrlDto, String redirectUrlId);
    Uni<Response> deactivateRedirectUrl(String redirectUrlId);
    Uni<Response> activateRedirectUrl(String redirectUrlId);
}
