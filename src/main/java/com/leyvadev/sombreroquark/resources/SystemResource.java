package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.dto.AllowedRedirectUrlDto;
import com.leyvadev.sombreroquark.dto.GroupDTO;
import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import com.leyvadev.sombreroquark.services.SystemService;
import com.leyvadev.sombreroquark.utils.Permissions;
import com.leyvadev.sombreroquark.utils.VerifyPermisionsInGroups;
import io.quarkus.security.Authenticated;
import io.quarkus.security.User;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/api/system")
@Produces("application/json")
@Consumes("application/json")
public class SystemResource {

    @Inject
    JsonWebToken jwt;
    @Inject
    VerifyPermisionsInGroups verifyPermisionsInGroups;
    @Inject
    SystemService systemService;

    @GET
    @Authenticated
    @Path("/permissions")
    public Uni<Response> getPermissions(@Context SecurityContext securityContext) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.VIEW_PERMISSIONS};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.getPermissions());
    }

    @GET
    @Authenticated
    @Path("/groups")
    public Uni<Response> getGroups(@Context SecurityContext securityContext) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.VIEW_GROUPS};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.getGroups());
    }

    @POST
    @Authenticated
    @Path("/groups")
    public Uni<Response> createGroup(@Context SecurityContext securityContext, GroupDTO groupDTO) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.CREATE_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.createGroup(groupDTO));
    }
    @PUT
    @Authenticated
    @Path("/groups/{groupId}")
    public Uni<Response> updateGroup(@Context SecurityContext securityContext, @PathParam("groupId") String groupId, GroupDTO groupDTO) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.updateGroup( groupDTO, groupId));
    }

    @PUT
    @Authenticated
    @Path("/groups/{groupId}/permissions/{permissionId}")
    public Uni<Response> addGroupPermission(@Context SecurityContext securityContext, @PathParam("groupId") String groupId, @PathParam("permissionId") String permissionId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_GROUP, Permissions.ASSIGN_PERMISSION_TO_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.addGroupPermission(groupId, permissionId));
    }

    @DELETE
    @Authenticated
    @Path("/groups/{groupId}/permissions/{permissionId}")
    public Uni<Response> removeGroupPermission(@Context SecurityContext securityContext, @PathParam("groupId") String groupId, @PathParam("permissionId") String permissionId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_GROUP, Permissions.REMOVE_PERMISSION_FROM_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.removeGroupPermission(groupId, permissionId));
    }

    @POST
    @Authenticated
    @Path("/users/{userId}/groups/{groupId}")
    public Uni<Response> addUserToGroup(@Context SecurityContext securityContext, @PathParam("userId") String userId, @PathParam("groupId") String groupId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_GROUP, Permissions.ASSIGN_USER_TO_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroupsAndPriority(groups, permissions, groupId)
                .chain(() -> systemService.addUserToGroup(userId, groupId));
    }

    @DELETE
    @Authenticated
    @Path("/users/{userId}/groups/{groupId}")
    public Uni<Response> removeUserFromGroup(@Context SecurityContext securityContext, @PathParam("userId") String userId, @PathParam("groupId") String groupId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_GROUP, Permissions.REMOVE_USER_FROM_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroupsAndPriority(groups, permissions, groupId)
                .chain(() -> systemService.removeUserFromGroup(userId, groupId));
    }

    @POST
    @Authenticated
    @Path("/redirect/list")
    public Uni<Response> getRedirect(@Context SecurityContext securityContext, PaginatedRequestDTO paginatedRequestDTO) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.VIEW_REDIRECT_URLS};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.getRedirectUrls(paginatedRequestDTO));
    }


    @POST
    @Authenticated
    @Path("/redirect")
    public Uni<Response> addRedirect(@Context SecurityContext securityContext, AllowedRedirectUrlDto redirect) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.CREATE_REDIRECT_URL};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.createRedirectUrl(redirect));
    }

    @PUT
    @Authenticated
    @Path("/redirect/{redirectId}")
    public Uni<Response> updateRedirect(@Context SecurityContext securityContext, @PathParam("redirectId") String redirectId, AllowedRedirectUrlDto redirect) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_REDIRECT_URL};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.updateRedirectUrl( redirect, redirectId));
    }

    @PUT
    @Authenticated
    @Path("/redirect/{redirectId}/activate")
    public Uni<Response> activateRedirect(@Context SecurityContext securityContext, @PathParam("redirectId") String redirectId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_REDIRECT_URL};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.activateRedirectUrl(redirectId));
    }

    @PUT
    @Authenticated
    @Path("/redirect/{redirectId}/deactivate")
    public Uni<Response> deactivateRedirect(@Context SecurityContext securityContext, @PathParam("redirectId") String redirectId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_REDIRECT_URL};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.deactivateRedirectUrl(redirectId));
    }




}
