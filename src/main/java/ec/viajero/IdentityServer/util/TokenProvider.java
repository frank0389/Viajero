package ec.viajero.IdentityServer.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import ec.viajero.IdentityServer.model.security.CustomUserDetail;

@Slf4j
@Component
@NoArgsConstructor
public class TokenProvider {
   
    private static final String AUTHORITIES_KEY = "auth";
    private static final String DNI = "dni";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String FIRSTNAME = "firstName";
    private static final String LASTNAME = "lastName";
    private static final String LANGKEY = "langKey";

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private int expiration;

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        CustomUserDetail principal = (CustomUserDetail) authentication.getPrincipal();
        
        return Jwts
                .builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(DNI, principal.getDni())
                .claim(EMAIL, principal.getEmail())
                .claim(PHONE, principal.getPhone())
                .claim(FIRSTNAME, principal.getFirstName())
                .claim(LASTNAME, principal.getLastName())
                .claim(LANGKEY, principal.getLangKey())
                .setIssuedAt(new Date())
                .setExpiration(new Date(now + (expiration * 1000))) 
                .signWith(getKey(secret))
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey(secret)).build().parseClaimsJws(token).getBody();
    }


    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getKey(secret)).build().parseClaimsJws(token).getBody();

        List<GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        String firstName = claims.get(FIRSTNAME).toString(); 
        String lastName = claims.get(LASTNAME).toString();
        String dni = claims.get(DNI).toString();
        String email = claims.get(EMAIL).toString();
        String phone = claims.get(PHONE).toString();
        String langKey = claims.get(LANGKEY).toString();
        CustomUserDetail principal = new CustomUserDetail(claims.getSubject(), "",
        firstName,lastName,dni,email,phone,langKey,authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String isValidToken(String jwt) {
         try {
            Jwts.parserBuilder().setSigningKey(getKey(secret)).build().parseClaimsJws(jwt);
            return "Token is Valid";
        }  catch (ExpiredJwtException e) {
            log.error("token expired");
            return "Token expired";
        } catch (UnsupportedJwtException e) {
            log.error("token unsupported");
              return "Token unsupported";
        } catch (MalformedJwtException e) {
            log.error("token malformed");
             return "Token malformed";
        }  catch (IllegalArgumentException e) {
            log.error("illegal args");
             return "Token with illegal args";
        } catch (Exception e) {
            log.error("Unknow error");
            return "Unknow error";
        }
    }


    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey(secret)).build().parseClaimsJws(authToken);
            return true;
        }  catch (ExpiredJwtException e) {
            log.error("token expired");
        } catch (UnsupportedJwtException e) {
            log.error("token unsupported");
        } catch (MalformedJwtException e) {
            log.error("token malformed");
        }  catch (IllegalArgumentException e) {
            log.error("illegal args");
        } catch (Exception e) {
            log.error("Unknow error");
        }
        return false;
    } 

    private Key getKey(String secret) {
        byte[] secretBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}