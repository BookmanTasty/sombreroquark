package com.leyvadev.sombreroquark.repositories;

import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;
@ApplicationScoped
public class SombreroGroupPermissionRepository {
    @Inject
    Mutiny.SessionFactory sessionFactory;

    public Uni<Void> addPermisionToGroupByUUID (UUID permissionId, UUID groupId) {
        String nativeQuery = "INSERT INTO sombrero_group_permissions (permission_id, group_id) VALUES (?, ?)";
        return sessionFactory.withTransaction((session, tx) -> session.createNativeQuery(nativeQuery)
                .setParameter(1, permissionId)
                .setParameter(2, groupId)
                .executeUpdate()
                .chain(() -> Uni.createFrom().nullItem()));
    }

    public Uni<Void> removePermisionToGroupByUUID (UUID permissionId, UUID groupId) {
        String nativeQuery = "DELETE FROM sombrero_group_permissions WHERE permission_id = ? AND group_id = ?";
        return sessionFactory.withTransaction((session, tx) -> session.createNativeQuery(nativeQuery)
                .setParameter(1, permissionId)
                .setParameter(2, groupId)
                .executeUpdate()
                .chain(() -> Uni.createFrom().nullItem()));
    }
}
