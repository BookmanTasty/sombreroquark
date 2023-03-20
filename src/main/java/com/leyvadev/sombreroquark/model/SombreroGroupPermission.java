package com.leyvadev.sombreroquark.model;

import com.leyvadev.sombreroquark.model.idclasses.SombreroGroupPermissionId;

import javax.persistence.*;

@Entity
@Table(name = "sombrero_group_permissions")
@IdClass(SombreroGroupPermissionId.class)
public class SombreroGroupPermission {

    @Id
    @ManyToOne
    @JoinColumn(name = "group_id")
    private SombreroGroup group;

    @Id
    @ManyToOne
    @JoinColumn(name = "permission_id")
    private SombreroPermission permission;

    public SombreroGroupPermission() {}

    public SombreroGroupPermission(SombreroGroup group, SombreroPermission permission) {
        this.group = group;
        this.permission = permission;
    }

    public SombreroGroup getGroup() {
        return group;
    }

    public void setGroup(SombreroGroup group) {
        this.group = group;
    }

    public SombreroPermission getPermission() {
        return permission;
    }

    public void setPermission(SombreroPermission permission) {
        this.permission = permission;
    }

}