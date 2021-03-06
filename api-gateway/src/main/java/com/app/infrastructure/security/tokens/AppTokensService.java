package com.app.infrastructure.security.tokens;

import com.app.application.dto.type.Role;
import com.app.application.proxy.DoctorServiceProxy;
import com.app.application.proxy.PatientServiceProxy;
import com.app.infrastructure.security.dto.TokensDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AppTokensService {

    @Value("${jwt.access-token.expiration-time-ms}")
    private Long accessTokenExpirationTimeInMs;

    @Value("${jwt.refresh-token.expiration-time-ms}")
    private Long refreshTokenExpirationTimeInMs;

    @Value("${jwt.token.prefix}")
    private String jwtTokenPrefix;

    @Value("${jwt.refresh-token.access-token-key}")
    private String refreshTokenAccessTokenKey;

    private final SecretKey secretKey;
    private final DoctorServiceProxy doctorServiceProxy;
    private final PatientServiceProxy patientServiceProxy;

    public Mono<TokensDto> generateTokens(User user, String role) {

        if (user == null) {
            throw new SecurityException("generate tokens - authentication object is null");
        }

        if (role == null) {
            throw new SecurityException("generate tokens - role is null");
        }

        return (switch (Role.valueOf(role)) {
            case ROLE_DOCTOR -> doctorServiceProxy.getDoctorByUsername(user.getUsername());
            case ROLE_PATIENT -> patientServiceProxy.getPatientByUsername(user.getUsername());
        })
                .flatMap(userFromDb -> {
                    var id = userFromDb.getId();
                    var createdDate = new Date();
                    var accessTokenExpirationTimeMillis = System.currentTimeMillis() + accessTokenExpirationTimeInMs;
                    var accessTokenExpirationTime = new Date(accessTokenExpirationTimeMillis);
                    var refreshTokenExpirationTime = new Date(System.currentTimeMillis() + refreshTokenExpirationTimeInMs);

                    var accessToken = Jwts
                            .builder()
                            .setSubject(String.valueOf(id))
                            .claim("role", role)
                            .setExpiration(accessTokenExpirationTime)
                            .setIssuedAt(createdDate)
                            .signWith(secretKey)
                            .compact();


                    var refreshToken = Jwts
                            .builder()
                            .setSubject(String.valueOf(id))
                            .setExpiration(refreshTokenExpirationTime)
                            .claim("role", role)
                            .setIssuedAt(createdDate)
                            .signWith(secretKey)
                            .claim(refreshTokenAccessTokenKey, accessTokenExpirationTimeMillis)
                            .compact();

                    return Mono.just(TokensDto
                            .builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build());
                });
    }

    private Claims claims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getRole(String token) {
        token = token.substring(7);
        return claims(token).get("role", String.class);
    }

    public String getId(String token) {
        token = token.substring(7);
        return claims(token).getSubject();
    }

    private Date getExpiration(String token) {
        return claims(token).getExpiration();
    }

    public boolean isTokenValid(String token) {

        token = token.substring(7);
        Date expirationDate = getExpiration(token);
        return expirationDate.after(new Date());
    }

}
