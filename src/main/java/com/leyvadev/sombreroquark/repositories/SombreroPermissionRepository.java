package com.leyvadev.sombreroquark.repositories;

import com.leyvadev.sombreroquark.model.SombreroPermission;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class SombreroPermissionRepository implements PanacheRepositoryBase<SombreroPermission, UUID> {
    @Inject
    Mutiny.SessionFactory sessionFactory;

    public Uni<Void> persistPermissions(List<String> permissions) {
        String nativeQuery = "INSERT INTO sombrero_permissions (id, name) VALUES (?, ?) "
                + "ON CONFLICT DO NOTHING";
        return sessionFactory.withTransaction((session, tx) -> {
            for (String permission : permissions) {
                UUID id = UUID.randomUUID();
                session.createNativeQuery(nativeQuery)
                        .setParameter(1, id)
                        .setParameter(2, permission)
                        .executeUpdate();
            }
            return Uni.createFrom().voidItem();
        });
    }

    public Uni<SombreroPermission> save(SombreroPermission permission) {
        return sessionFactory.withTransaction((session, tx) -> session.merge(permission));
    }

    public Uni<List<String>> findMissingPermissions(List<String> permissions) {
        return sessionFactory.withSession(session ->
                session.createQuery("SELECT p.name FROM SombreroPermission p WHERE p.name IN :permissions", String.class)
                        .setParameter("permissions", permissions)
                        .getResultList()
        ).onItem().transform(result -> {
            Set<String> existingPermissions = new HashSet<>(result);
            List<String> missingPermissions = new ArrayList<>();
            for (String permission : permissions) {
                if (!existingPermissions.contains(permission)) {
                    missingPermissions.add(permission);
                }
            }
            return missingPermissions;
        });
    }


}
