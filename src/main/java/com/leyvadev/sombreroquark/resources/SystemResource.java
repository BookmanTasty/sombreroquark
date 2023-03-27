package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.dto.AllowedRedirectUrlDto;
import com.leyvadev.sombreroquark.dto.GroupDTO;
import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import com.leyvadev.sombreroquark.dto.PaginatedURLResponseDTO;
import com.leyvadev.sombreroquark.services.SystemService;
import com.leyvadev.sombreroquark.utils.Permissions;
import com.leyvadev.sombreroquark.utils.VerifyPermisionsInGroups;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

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
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "Permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"permissions\": [\n    \"view_permissions\",\n    \"view_groups\",\n    \"create_group\",\n    \"update_group\",\n    \"delete_group\",\n    \"view_users\",\n    \"create_user\",\n    \"update_user\",\n    \"delete_user\",\n    \"view_allowed_redirect_urls\",\n    \"create_allowed_redirect_url\",\n    \"update_allowed_redirect_url\",\n    \"delete_allowed_redirect_url\"\n  ]\n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> getPermissions(@Context SecurityContext securityContext) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.VIEW_PERMISSIONS};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.getPermissions());
    }

    @GET
    @Authenticated
    @Path("/groups")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "Groups",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "[{\"id\":\"93e409f1-a8ba-41f3-a57e-743a5b3e0e45\",\"name\":\"SombreroAdmin\",\"data\":\"SombreroAdmin default group\",\"priority\":0,\"permissions\":[\"assign_role_to_user\",\"delete_role\",\"update_permission\",\"create_group\",\"remove_group_from_user\",\"view_redirect_urls\",\"deactivate_redirect_url\",\"create_role\",\"assign_group_to_user\",\"reset_password\",\"create_redirect_url\",\"update_redirect_url\",\"remove_user_from_group\",\"change_password\",\"view_audit_logs\",\"update_user\",\"update_group\",\"read_group\",\"create_permission\",\"export_data\",\"view_groups\",\"assign_user_to_group\",\"read_user\",\"read_permission\",\"delete_group\",\"read_role\",\"remove_permission_from_group\",\"view_permissions\",\"assign_permission_to_group\",\"delete_permission\",\"delete_user\",\"view_roles\",\"view_users\",\"update_role\",\"create_user\",\"remove_role_from_user\"]}]"                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> getGroups(@Context SecurityContext securityContext) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.VIEW_GROUPS};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.getGroups());
    }

    @POST
    @Authenticated
    @Path("/groups")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "Group created",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"id\": \"93e409f1-a8ba-41f3-a57e-743a5b3e0e45\",\n  \"name\": \"SombreroAdmin\",\n  \"data\": \"SombreroAdmin default group\",\n  \"priority\": 0,\n  \"permissions\": [\n    \"assign_role_to_user\",\n    \"delete_role\",\n    \"update_permission\",\n    \"create_group\",\n    \"remove_group_from_user\",\n    \"view_redirect_urls\",\n    \"deactivate_redirect_url\",\n    \"create_role\",\n    \"assign_group_to_user\",\n    \"reset_password\",\n    \"create_redirect_url\",\n    \"update_redirect_url\",\n    \"remove_user_from_group\",\n    \"change_password\",\n    \"view_audit_logs\",\n    \"update_user\",\n    \"update_group\",\n    \"read_group\",\n    \"create_permission\",\n    \"export_data\",\n    \"view_groups\",\n    \"assign_user_to_group\",\n    \"read_user\",\n    \"read_permission\",\n    \"delete_group\",\n    \"read_role\",\n    \"remove_permission_from_group\",\n    \"view_permissions\",\n    \"assign_permission_to_group\",\n    \"delete_permission\",\n    \"delete_user\",\n    \"view_roles\",\n    \"view_users\",\n    \"update_role\",\n    \"create_user\",\n    \"remove_role_from_user\"\n  ]\n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Group already exists",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Group already exists\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid priority",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Group priority is too low\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> createGroup(@Context SecurityContext securityContext, GroupDTO groupDTO) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.CREATE_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroupsAndPriority(groups, permissions, groupDTO.getPriority())
                .chain(() -> systemService.createGroup(groupDTO));
    }
    @PUT
    @Authenticated
    @Path("/groups/{groupId}")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "Group updated",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"id\": \"93e409f1-a8ba-41f3-a57e-743a5b3e0e45\",\n  \"name\": \"SombreroAdmin\",\n  \"data\": \"SombreroAdmin default group\",\n  \"priority\": 0,\n  \"permissions\": [\n    \"assign_role_to_user\",\n    \"delete_role\",\n    \"update_permission\",\n    \"create_group\",\n    \"remove_group_from_user\",\n    \"view_redirect_urls\",\n    \"deactivate_redirect_url\",\n    \"create_role\",\n    \"assign_group_to_user\",\n    \"reset_password\",\n    \"create_redirect_url\",\n    \"update_redirect_url\",\n    \"remove_user_from_group\",\n    \"change_password\",\n    \"view_audit_logs\",\n    \"update_user\",\n    \"update_group\",\n    \"read_group\",\n    \"create_permission\",\n    \"export_data\",\n    \"view_groups\",\n    \"assign_user_to_group\",\n    \"read_user\",\n    \"read_permission\",\n    \"delete_group\",\n    \"read_role\",\n    \"remove_permission_from_group\",\n    \"view_permissions\",\n    \"assign_permission_to_group\",\n    \"delete_permission\",\n    \"delete_user\",\n    \"view_roles\",\n    \"view_users\",\n    \"update_role\",\n    \"create_user\",\n    \"remove_role_from_user\"\n  ]\n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "UUID is not valid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"UUID is not valid\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid priority",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Group priority is too low\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @RequestBody
    public Uni<Response> updateGroup(@Context SecurityContext securityContext, @PathParam("groupId") String groupId, GroupDTO groupDTO) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroupsAndPriority(groups, permissions, groupDTO.getPriority(), groupId)
                .chain(() -> systemService.updateGroup( groupDTO, groupId));
    }

    @PUT
    @Authenticated
    @Path("/groups/{groupId}/permissions/{permissionId}")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "Permission added to group",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"message\": \"Permission added to group\",\n  \"code\": 200 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "UUID is not valid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"UUID is not valid\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> addGroupPermission(@Context SecurityContext securityContext, @PathParam("groupId") String groupId, @PathParam("permissionId") String permissionId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_GROUP, Permissions.ASSIGN_PERMISSION_TO_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.addGroupPermission(groupId, permissionId));
    }

    @DELETE
    @Authenticated
    @Path("/groups/{groupId}/permissions/{permissionId}")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "Permission removed from group",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"message\": \"Permission removed from group\",\n  \"code\": 200 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "UUID is not valid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"UUID is not valid\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> removeGroupPermission(@Context SecurityContext securityContext, @PathParam("groupId") String groupId, @PathParam("permissionId") String permissionId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_GROUP, Permissions.REMOVE_PERMISSION_FROM_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.removeGroupPermission(groupId, permissionId));
    }

    @POST
    @Authenticated
    @Path("/users/{userId}/groups/{groupId}")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "User added to group",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"message\": \"User added to group\",\n  \"code\": 200 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "UUID is not valid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"UUID is not valid\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> addUserToGroup(@Context SecurityContext securityContext, @PathParam("userId") String userId, @PathParam("groupId") String groupId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_GROUP, Permissions.ASSIGN_USER_TO_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroupsAndPriority(groups, permissions, groupId)
                .chain(() -> systemService.addUserToGroup(userId, groupId));
    }

    @DELETE
    @Authenticated
    @Path("/users/{userId}/groups/{groupId}")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "User removed from group",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"message\": \"User removed from group\",\n  \"code\": 200 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "UUID is not valid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"UUID is not valid\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> removeUserFromGroup(@Context SecurityContext securityContext, @PathParam("userId") String userId, @PathParam("groupId") String groupId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_GROUP, Permissions.REMOVE_USER_FROM_GROUP};

        return verifyPermisionsInGroups.verifyPermissionsInGroupsAndPriority(groups, permissions, groupId)
                .chain(() -> systemService.removeUserFromGroup(userId, groupId));
    }

    @POST
    @Authenticated
    @Path("/redirect/list")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "List of redirect urls",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = PaginatedURLResponseDTO.class,
                            example = "{\n  \"data\": [\n    {\n      \"id\": \"string\",\n      \"url\": \"string\"\n    }\n  ],\n  \"total\": 0\n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> getRedirect(@Context SecurityContext securityContext, PaginatedRequestDTO paginatedRequestDTO) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.VIEW_REDIRECT_URLS};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.getRedirectUrls(paginatedRequestDTO));
    }


    @POST
    @Authenticated
    @Path("/redirect")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "Redirect url created",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"message\": \"Redirect url created\",\n  \"code\": 200 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> addRedirect(@Context SecurityContext securityContext, AllowedRedirectUrlDto redirect) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.CREATE_REDIRECT_URL};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.createRedirectUrl(redirect));
    }

    @PUT
    @Authenticated
    @Path("/redirect/{redirectId}")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "Redirect url updated",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"message\": \"Redirect url updated\",\n  \"code\": 200 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "UUID is not valid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"UUID is not valid\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> updateRedirect(@Context SecurityContext securityContext, @PathParam("redirectId") String redirectId, AllowedRedirectUrlDto redirect) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_REDIRECT_URL};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.updateRedirectUrl( redirect, redirectId));
    }

    @PUT
    @Authenticated
    @Path("/redirect/{redirectId}/activate")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "Redirect url activated",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"message\": \"Redirect url activated\",\n  \"code\": 200 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "UUID is not valid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"UUID is not valid\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> activateRedirect(@Context SecurityContext securityContext, @PathParam("redirectId") String redirectId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_REDIRECT_URL};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.activateRedirectUrl(redirectId));
    }

    @PUT
    @Authenticated
    @Path("/redirect/{redirectId}/deactivate")
    @Tag(name = "System")
    @APIResponse(
            responseCode = "200",
            description = "Redirect url deactivated",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"message\": \"Redirect url deactivated\",\n  \"code\": 200 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing permissions",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"Missing permissions\",\n  \"code\": 400 \n}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "UUID is not valid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = Response.class,
                            example = "{\n  \"error\": \"UUID is not valid\",\n  \"code\": 400 \n}"
                    )
            )
    )
    public Uni<Response> deactivateRedirect(@Context SecurityContext securityContext, @PathParam("redirectId") String redirectId) {
        String[] groups = jwt.getGroups().toArray(String[]::new);
        String[] permissions = new String[]{Permissions.UPDATE_REDIRECT_URL};

        return verifyPermisionsInGroups.verifyPermissionsInGroups(groups, permissions)
                .chain(() -> systemService.deactivateRedirectUrl(redirectId));
    }


}
