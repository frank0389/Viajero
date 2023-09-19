package ec.viajero.IdentityServer.util;
import static org.assertj.core.api.Assertions.assertThat;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import ec.viajero.IdentityServer.model.security.CustomUserDetail;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


public class TokenProviderTest {
    private TokenProvider tokenProvider;

    @BeforeEach
    public void setup() {
         tokenProvider = new TokenProvider();
         String secret = "OWUzZmRlNjI2MTlkNTYyYjQwZGU1YzhkNTVlMDJmYzRlODRiOGRmZmQxYjc3NWNkZGFjN2QxNWMyNWQ2ZTljNzUyNTExZDVlOWZjMDhkNzZkODg0YWZhNWRlZDM0Y2NiYWY4ZGJlZmUxMDM1YTc0NDAxNmYxYzk3NTY3MjE2ZTI=";
         ReflectionTestUtils.setField(tokenProvider, "secret", secret );
         ReflectionTestUtils.setField(tokenProvider, "expiration", 3600 );  // one minute
    }

    @Test
    void testReturnFalseWhenJWThasInvalidSignature() {
        boolean isTokenValid = tokenProvider.validateToken(createTokenWithDifferentSignature());

        assertThat(isTokenValid).isFalse();
    }

    @Test
    void testReturnFalseWhenJWTisMalformed() {
        Authentication authentication = createAuthentication();
        String token = tokenProvider.createToken(authentication);
        String invalidToken = token.substring(1);
        boolean isTokenValid = tokenProvider.validateToken(invalidToken);
        assertThat(isTokenValid).isFalse();
    }

    @Test
    void testReturnFalseWhenJWTisExpired() {
        String token = createExpiredToken();
        boolean isTokenValid = tokenProvider.validateToken(token);
        assertThat(isTokenValid).isFalse();
    }

    @Test
    void testReturnFalseWhenJWTisUnsupported() {
        String unsupportedToken = createUnsupportedToken();
        boolean isTokenValid = tokenProvider.validateToken(unsupportedToken);
        assertThat(isTokenValid).isFalse();
    }
    

     @Test
    void testReturnTrueForValidToken() {
        String token = createValidToken();
        boolean isTokenValid = tokenProvider.validateToken(token);
        assertThat(isTokenValid).isTrue();
    }

    @Test
    void testReturnFalseWhenJWTisInvalid() {
        boolean isTokenValid = tokenProvider.validateToken("");
        assertThat(isTokenValid).isFalse();
    }

    private Authentication createAuthentication() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("anonymous"));
        CustomUserDetail principal = new CustomUserDetail("ssss", "12345gtr*", "Enrique", 
        "Nicolau", "23456789032", "emai.gemail.com", "1234567", "es",  authorities);
        return new UsernamePasswordAuthenticationToken(principal, "anonymous", authorities);
    }

    private String createUnsupportedToken() {
         Key key = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode("Xfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8")
        );
        return Jwts.builder().setPayload("payload").signWith(key, SignatureAlgorithm.HS512).compact();
    }

    private String createTokenWithDifferentSignature() {
        Key otherKey = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode("Xfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8")
        );

        return Jwts
            .builder()
            .setSubject("anonymous")
            .signWith(otherKey, SignatureAlgorithm.HS512)
            .setExpiration(new Date(new Date().getTime() + 60000))
            .compact();
    } 

    private String createValidToken() {
        Key otherKey = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode("OWUzZmRlNjI2MTlkNTYyYjQwZGU1YzhkNTVlMDJmYzRlODRiOGRmZmQxYjc3NWNkZGFjN2QxNWMyNWQ2ZTljNzUyNTExZDVlOWZjMDhkNzZkODg0YWZhNWRlZDM0Y2NiYWY4ZGJlZmUxMDM1YTc0NDAxNmYxYzk3NTY3MjE2ZTI=")
        );

        return Jwts
            .builder()
            .setSubject("anonymous")
            .setExpiration(new Date(new Date().getTime() + 60000))
            .signWith(otherKey, SignatureAlgorithm.HS512)
            .compact();
    } 

    private String createExpiredToken() {
        Key otherKey = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode("OWUzZmRlNjI2MTlkNTYyYjQwZGU1YzhkNTVlMDJmYzRlODRiOGRmZmQxYjc3NWNkZGFjN2QxNWMyNWQ2ZTljNzUyNTExZDVlOWZjMDhkNzZkODg0YWZhNWRlZDM0Y2NiYWY4ZGJlZmUxMDM1YTc0NDAxNmYxYzk3NTY3MjE2ZTI=")
        );

        return Jwts
            .builder()
            .setSubject("anonymous")
            .setExpiration(new Date(new Date().getTime() +(-3600*1000)))
            .signWith(otherKey, SignatureAlgorithm.HS512)
            .compact();
    } 

    

}