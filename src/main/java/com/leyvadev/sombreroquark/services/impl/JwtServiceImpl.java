package com.leyvadev.sombreroquark.services.impl;

import com.leyvadev.sombreroquark.exceptionmappers.IllegalArgumentExceptionWithTemplate;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.services.JwtService;
import com.leyvadev.sombreroquark.utils.JwtUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class JwtServiceImpl implements JwtService {
    private static final Logger LOGGER = Logger.getLogger(JwtServiceImpl.class);

    private static final String EMAIL_AUDIENCE = "email";
    private static final String ACCESS_AUDIENCE = "access";
    private static final String REFRESH_AUDIENCE = "refresh";
    private static final String RESET_AUDIENCE = "reset";
    private static final String ERROR_CREATING_TOKEN = "Error creating token";
    private static final String USER_ID = "userId";
    private static final String INVALID_TOKEN = "Invalid token";
    @Inject
    JwtUtils jwtUtils;
    @ConfigProperty(name = "smallrye.jwt.issuer")
    String issuer;
    @Override
    public String generateEmailConfirmationToken(SombreroUser user) {
        RsaJsonWebKey rsaJsonWebKey = jwtUtils.getRsaJsonWebKey();

        JwtClaims claims = new JwtClaims();
        claims.setIssuer(issuer);
        claims.setAudience(EMAIL_AUDIENCE);
        claims.setSubject(user.getEmail());
        claims.setClaim(USER_ID, user.getId());
        claims.setClaim(EMAIL_AUDIENCE, user.getEmail());
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
            LOGGER.error(ERROR_CREATING_TOKEN, e);
            return null;
        }
    }
    public String verifyEmailConfirmationToken(String token) {
        RsaJsonWebKey rsaJsonWebKey = jwtUtils.getRsaJsonWebKey();
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setExpectedAudience(EMAIL_AUDIENCE)
                .setExpectedIssuer(issuer)
                .setVerificationKey(rsaJsonWebKey.getKey())
                .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, rsaJsonWebKey.getAlgorithm())
                .build();
        try {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            return jwtClaims.getSubject();
        } catch ( MalformedClaimException e) {
            LOGGER.error(INVALID_TOKEN, e);
            throw new IllegalArgumentExceptionWithTemplate("Invalid link",null);
        } catch (InvalidJwtException e) {
            LOGGER.error(INVALID_TOKEN, e);
            throw new  IllegalArgumentExceptionWithTemplate("Expired link",null);
        }
    }

    @Override
    public String verifyRefreshToken(String token) {
        RsaJsonWebKey rsaJsonWebKey = jwtUtils.getRsaJsonWebKey();
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setExpectedAudience(REFRESH_AUDIENCE)
                .setExpectedIssuer(issuer)
                .setVerificationKey(rsaJsonWebKey.getKey())
                .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, rsaJsonWebKey.getAlgorithm())
                .build();
        try {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            return jwtClaims.getSubject();
        } catch (InvalidJwtException | MalformedClaimException e) {
            LOGGER.error(INVALID_TOKEN, e);
            throw new IllegalArgumentException(INVALID_TOKEN);
        }
    }

    @Override
    public String verifyResetPasswordToken(String token) {
        RsaJsonWebKey rsaJsonWebKey = jwtUtils.getRsaJsonWebKey();
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setExpectedAudience(RESET_AUDIENCE)
                .setExpectedIssuer(issuer)
                .setVerificationKey(rsaJsonWebKey.getKey())
                .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, rsaJsonWebKey.getAlgorithm())
                .build();
        try {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            return jwtClaims.getSubject();
        } catch (InvalidJwtException | MalformedClaimException e) {
            LOGGER.error(INVALID_TOKEN, e);
            throw new IllegalArgumentException(INVALID_TOKEN);
        }
    }

    @Override
    public String generateAccessToken(SombreroUser user) {
        RsaJsonWebKey rsaJsonWebKey = jwtUtils.getRsaJsonWebKey();
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(issuer);
        claims.setAudience(ACCESS_AUDIENCE);
        claims.setSubject(user.getEmail());
        claims.setClaim(USER_ID, user.getId());
        claims.setClaim(EMAIL_AUDIENCE, user.getEmail());
        claims.setClaim("groups", user.getGroupsAsList());
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setExpirationTimeMinutesInTheFuture(15);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setAlgorithmHeaderValue(rsaJsonWebKey.getAlgorithm());

        try {
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            LOGGER.error(ERROR_CREATING_TOKEN, e);
            throw new IllegalArgumentException(ERROR_CREATING_TOKEN);
        }
    }

    @Override
    public String generateRefreshToken(SombreroUser user) {
        RsaJsonWebKey rsaJsonWebKey = jwtUtils.getRsaJsonWebKey();
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(issuer);
        claims.setAudience(REFRESH_AUDIENCE);
        claims.setSubject(user.getEmail());
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setExpirationTimeMinutesInTheFuture(10080);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setAlgorithmHeaderValue(rsaJsonWebKey.getAlgorithm());

        try {
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            LOGGER.error(ERROR_CREATING_TOKEN, e);
            throw new IllegalArgumentException(ERROR_CREATING_TOKEN);
        }
    }

    @Override
    public String generateResetPasswordToken(SombreroUser user) {
        RsaJsonWebKey rsaJsonWebKey = jwtUtils.getRsaJsonWebKey();
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(issuer);
        claims.setAudience(RESET_AUDIENCE);
        claims.setSubject(user.getEmail());
        claims.setClaim(USER_ID, user.getId());
        claims.setClaim(EMAIL_AUDIENCE, user.getEmail());
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
            LOGGER.error(ERROR_CREATING_TOKEN, e);
            throw new IllegalArgumentException(ERROR_CREATING_TOKEN);
        }
    }

    @Override
    public String generatePasswordResetToken(SombreroUser user) {
        RsaJsonWebKey rsaJsonWebKey = jwtUtils.getRsaJsonWebKey();

        JwtClaims claims = new JwtClaims();
        claims.setIssuer(issuer);
        claims.setAudience(EMAIL_AUDIENCE);
        claims.setSubject(user.getEmail());
        claims.setClaim(USER_ID, user.getId());
        claims.setClaim(EMAIL_AUDIENCE, user.getEmail());
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
            LOGGER.error(ERROR_CREATING_TOKEN, e);
            throw new IllegalArgumentException(ERROR_CREATING_TOKEN);
        }
    }
}
