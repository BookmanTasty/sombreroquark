package com.leyvadev.sombreroquark.services.impl;

import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.services.JwtService;
import com.leyvadev.sombreroquark.utils.JwtUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
@ApplicationScoped
public class JwtServiceImpl implements JwtService {
    private static final Logger LOGGER = Logger.getLogger(JwtServiceImpl.class);

    @Inject
    JwtUtils jwtUtils;
    @ConfigProperty(name = "smallrye.jwt.issuer")
    String issuer;
    @Override
    public String generateEmailConfirmationToken(SombreroUser user) {
        RsaJsonWebKey rsaJsonWebKey = jwtUtils.getRsaJsonWebKey();
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(issuer);
        claims.setSubject(user.getEmail());
        claims.setClaim("userId", user.getId());
        claims.setClaim("email", user.getEmail());
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setExpirationTimeMinutesInTheFuture(60);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setAlgorithmHeaderValue(rsaJsonWebKey.getAlgorithm());

        try {
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            LOGGER.error("Error creating token", e);
            return null;
        }
    }
}
