package com.leyvadev.sombreroquark.repositories;

import com.leyvadev.sombreroquark.model.SombreroAllowedRedirectUrl;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;
@ApplicationScoped
public class SombreroAllowedRedirectUrlsRepository implements PanacheRepositoryBase<SombreroAllowedRedirectUrl, UUID> {
    @Inject
    Mutiny.SessionFactory sessionFactory;
    public Uni<SombreroAllowedRedirectUrl> existsByUrlAndIsActive(String url) {
        return this.find("url = ?1 and isActive = true", url).firstResult();
    }
    public Uni<SombreroAllowedRedirectUrl> findByUrl(String url) {
        return this.find("url = ?1", url).firstResult();
    }
    public Uni<SombreroAllowedRedirectUrl> save(SombreroAllowedRedirectUrl allowedRedirectUrl) {
        return sessionFactory.withTransaction((session, tx) -> session.merge(allowedRedirectUrl));
    }
}
