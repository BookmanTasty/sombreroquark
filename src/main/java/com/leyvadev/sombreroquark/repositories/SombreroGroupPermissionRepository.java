package com.leyvadev.sombreroquark.repositories;

import io.smallrye.mutiny.Uni;

import io.vertx.sqlclient.Tuple;

import org.hibernate.reactive.mutiny.Mutiny.Query;

import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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

    public Uni<List<String>> getMissingPermissions(List<String> groups, List<String> permissions) {
        String query = "SELECT DISTINCT p.name" +
                " FROM sombrero_permissions p" +
                " INNER JOIN sombrero_group_permissions gp ON gp.permission_id = p.id" +
                " INNER JOIN sombrero_groups g ON g.id = gp.group_id" +
                " WHERE g.name IN (:groups)";
        return sessionFactory.withSession(session -> {
            Query<String> q = session.createNativeQuery(query,String.class);
            q.setParameter("groups", groups);
            return q.getResultList();
        }).onItem().transform(result -> {
            List<String> missingPermissions = new ArrayList<>();
            for (String permission : permissions) {
                if (!result.contains(permission)) {
                    missingPermissions.add(permission);
                }
            }
            return missingPermissions;
        });
        }
}
