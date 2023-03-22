package com.leyvadev.sombreroquark.utils;

import java.io.Serializable;
import java.util.List;

public final class Permissions implements Serializable {
    public static final String CREATE_USER = "create_user";
    public static final String READ_USER = "read_user";
    public static final String UPDATE_USER = "update_user";
    public static final String DELETE_USER = "delete_user";
    public static final String VIEW_USERS = "view_users";
    public static final String CREATE_ROLE = "create_role";
    public static final String READ_ROLE = "read_role";
    public static final String UPDATE_ROLE = "update_role";
    public static final String DELETE_ROLE = "delete_role";
    public static final String VIEW_ROLES = "view_roles";
    public static final String VIEW_PERMISSIONS = "view_permissions";
    public static final String CHANGE_PASSWORD = "change_password";
    public static final String RESET_PASSWORD = "reset_password";
    public static final String VIEW_GROUPS = "view_groups";
    public static final String CREATE_GROUP = "create_group";
    public static final String READ_GROUP = "read_group";
    public static final String UPDATE_GROUP = "update_group";
    public static final String DELETE_GROUP = "delete_group";
    public static final String ASSIGN_USER_TO_GROUP = "assign_user_to_group";
    public static final String REMOVE_USER_FROM_GROUP = "remove_user_from_group";
    public static final String ASSIGN_PERMISSION_TO_GROUP = "assign_permission_to_group";
    public static final String REMOVE_PERMISSION_FROM_GROUP = "remove_permission_from_group";
    public static final String CREATE_PERMISSION = "create_permission";
    public static final String READ_PERMISSION = "read_permission";
    public static final String UPDATE_PERMISSION = "update_permission";
    public static final String DELETE_PERMISSION = "delete_permission";
    public static final String ASSIGN_ROLE_TO_USER = "assign_role_to_user";
    public static final String REMOVE_ROLE_FROM_USER = "remove_role_from_user";
    public static final String ASSIGN_GROUP_TO_USER = "assign_group_to_user";
    public static final String REMOVE_GROUP_FROM_USER = "remove_group_from_user";
    public static final String VIEW_AUDIT_LOGS = "view_audit_logs";
    public static final String EXPORT_DATA = "export_data";

    public static List<String> getPermissions() {
        return List.of(
                CREATE_USER,
                READ_USER,
                UPDATE_USER,
                DELETE_USER,
                VIEW_USERS,
                CREATE_ROLE,
                READ_ROLE,
                UPDATE_ROLE,
                DELETE_ROLE,
                VIEW_ROLES,
                CHANGE_PASSWORD,
                RESET_PASSWORD,
                VIEW_GROUPS,
                CREATE_GROUP,
                READ_GROUP,
                UPDATE_GROUP,
                DELETE_GROUP,
                ASSIGN_USER_TO_GROUP,
                REMOVE_USER_FROM_GROUP,
                CREATE_PERMISSION,
                READ_PERMISSION,
                UPDATE_PERMISSION,
                DELETE_PERMISSION,
                ASSIGN_ROLE_TO_USER,
                REMOVE_ROLE_FROM_USER,
                ASSIGN_GROUP_TO_USER,
                REMOVE_GROUP_FROM_USER,
                ASSIGN_PERMISSION_TO_GROUP,
                REMOVE_PERMISSION_FROM_GROUP,
                VIEW_AUDIT_LOGS,
                EXPORT_DATA,
                VIEW_PERMISSIONS
        );
    }
}
