package com.leyvadev.sombreroquark.repositories;

import com.leyvadev.sombreroquark.model.SombreroGroup;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;
@ApplicationScoped
public class SombreroGroupRepository implements PanacheRepositoryBase<SombreroGroup, UUID> {

    @Inject
    Mutiny.SessionFactory sessionFactory;
    public Uni<SombreroGroup> findByName(String name) {
        return sessionFactory.withSession(session ->
                session.createQuery("SELECT g FROM SombreroGroup g WHERE g.name = :name", SombreroGroup.class)
                        .setParameter("name", name)
                        .getResultList()
                        .onItem()
                        .ifNotNull()
                        .transformToUni(list -> {
                            if (list.size() > 0) {
                                return Uni.createFrom().item(list.get(0));
                            } else {
                                return Uni.createFrom().nullItem();
                            }
                        })
        );
    }

    public Uni<SombreroGroup> save(SombreroGroup group) {
        return sessionFactory.withTransaction((session, tx) -> session.merge(group));
    }
}
