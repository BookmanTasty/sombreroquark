package com.leyvadev.sombreroquark.services;

import com.leyvadev.sombreroquark.dto.GroupDTO;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.core.Response;

public interface SystemService {
    Uni<Response> getPermissions();
    Uni<Response> getGroups();
    Uni<Response> createGroup(GroupDTO groupDTO);
    Uni<Response> addGroupPermission(String groupId, String permissionId);
    Uni<Response> removeGroupPermission(String groupId, String permissionId);
    Uni<Response> addUserToGroup(String userId, String groupId);
    Uni<Response> removeUserFromGroup(String userId, String groupId);
}
