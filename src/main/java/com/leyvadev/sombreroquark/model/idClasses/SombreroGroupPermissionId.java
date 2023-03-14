package com.leyvadev.sombreroquark.model.idClasses;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class SombreroGroupPermissionId implements Serializable {

    private UUID group;

    private UUID permission;

    public SombreroGroupPermissionId() {}

    public SombreroGroupPermissionId(UUID group, UUID permission) {
        this.group = group;
        this.permission = permission;
    }

    public UUID getGroup() {
        return group;
    }

    public void setGroup(UUID group) {
        this.group = group;
    }

    public UUID getPermission() {
        return permission;
    }

    public void setPermission(UUID permission) {
        this.permission = permission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SombreroGroupPermissionId)) return false;
        SombreroGroupPermissionId that = (SombreroGroupPermissionId) o;
        return Objects.equals(getGroup(), that.getGroup()) &&
                Objects.equals(getPermission(), that.getPermission());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroup(), getPermission());
    }

}
