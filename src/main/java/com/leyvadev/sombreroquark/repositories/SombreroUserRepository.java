package com.leyvadev.sombreroquark.repositories;

import com.leyvadev.sombreroquark.model.SombreroUser;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class SombreroUserRepository implements PanacheRepositoryBase<SombreroUser, UUID> {

    @Inject
    Mutiny.SessionFactory sessionFactory;

    public Uni<SombreroUser> findByEmail(String email) {
        return sessionFactory.withSession(session ->
                session.createQuery("SELECT u FROM SombreroUser u LEFT JOIN FETCH u.groups WHERE u.email = :email", SombreroUser.class)
                        .setParameter("email", email)
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

    public Uni<SombreroUser> persist(SombreroUser user) {
        return sessionFactory.withTransaction((session, tx) -> session.merge(user));
    }


}