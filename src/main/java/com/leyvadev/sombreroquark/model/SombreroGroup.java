package com.leyvadev.sombreroquark.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "sombrero_groups")
public class SombreroGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "groups")
    private Set<SombreroUser> users = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "sombrero_group_permission",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<SombreroPermission> permissions = new HashSet<>();

    @Column(name = "data", nullable = false)
    private String data;

    @Column(name = "is_permission_required", nullable = false)
    private boolean isPermissionRequired = false;

    public boolean isPermissionRequired() {
        return isPermissionRequired;
    }

    public void setPermissionRequired(boolean permissionRequired) {
        isPermissionRequired = permissionRequired;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<SombreroUser> getUsers() {
        return users;
    }

    public void setUsers(Set<SombreroUser> users) {
        this.users = users;
    }

    public Set<SombreroPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<SombreroPermission> permissions) {
        this.permissions = permissions;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
