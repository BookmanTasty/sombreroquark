package com.leyvadev.sombreroquark.repositories;

import com.leyvadev.sombreroquark.model.SombreroBlacklistToken;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;
@ApplicationScoped
public class SombreroBlacklistTokenRepository implements PanacheRepositoryBase<SombreroBlacklistToken, UUID> {
    @Inject
    Mutiny.SessionFactory sessionFactory;
    public Uni<SombreroBlacklistToken> findByToken(String token) {
        return this.find("token = ?1", token).firstResult();
    }

    public Uni<SombreroBlacklistToken> save(SombreroBlacklistToken blacklistToken) {
        return sessionFactory.withTransaction((session, tx) -> session.merge(blacklistToken));
    }

}
