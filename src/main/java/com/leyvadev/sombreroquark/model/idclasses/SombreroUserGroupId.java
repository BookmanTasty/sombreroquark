package com.leyvadev.sombreroquark.model.idclasses;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class SombreroUserGroupId implements Serializable {

    private UUID user;

    private UUID group;

    public SombreroUserGroupId() {}

    public SombreroUserGroupId(UUID user, UUID group) {
        this.user = user;
        this.group = group;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public UUID getGroup() {
        return group;
    }

    public void setGroup(UUID group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SombreroUserGroupId)) return false;
        SombreroUserGroupId that = (SombreroUserGroupId) o;
        return Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getGroup(), that.getGroup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getGroup());
    }

}