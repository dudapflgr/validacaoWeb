package utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class GeradorToken {

    private static final SecretKey CHAVE = Keys.hmacShaKeyFor(
            "Essa é a minha chave secreta que ninguém irá descobrir"
                    .getBytes(StandardCharsets.UTF_8));

    public String gerarTokenJwt(String userName) {
        String jwtToken = Jwts.builder()
                .setSubject(userName)
                .setIssuer("localhost:7000")
                .setIssuedAt(new Date())
                .setExpiration(
                        Date.from(
                                LocalDateTime.now().plusMinutes(10L)
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                .signWith(CHAVE)
                .compact();
        return jwtToken;
    }

    public boolean validaTokenJwt(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(CHAVE)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
