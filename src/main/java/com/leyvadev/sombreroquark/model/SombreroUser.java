package com.leyvadev.sombreroquark.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "sombrero_users")
@NamedEntityGraph(
        name = "userWithGroups",
        attributeNodes = {
                @NamedAttributeNode("groups")
        }
)
public class SombreroUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "data", columnDefinition = "jsonb")
    private JsonObject data ;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified = false;
    @ManyToMany
    @JoinTable(
            name = "sombrero_user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @JsonIgnore
    private Set<SombreroGroup> groups = new HashSet<>();

    public Set<SombreroGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<SombreroGroup> groups) {
        this.groups = groups;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public void setData(Map<String, Object> data) {
        this.data = JsonObject.mapFrom(data);
    }
    @JsonIgnore
    public Map<String, Object> getDataAsMap() {
        return data.getMap();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
    @JsonProperty("groups")
    public List<String> getGroupsAsList() {
        List<String> groupNames = new ArrayList<>();
        for (SombreroGroup group : groups) {
            groupNames.add(group.getName());
        }
        return groupNames;
    }
}
