package com.dmf.marketplace.compartilhado.seguranca;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenManager {

	private final SecretKey secretKey;
	private final long expirationInMillis;

	public TokenManager(@Value("${jwt.secret}") String secret,
						@Value("${jwt.expiration}") long expirationInMillis) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationInMillis = expirationInMillis;
	}

	public String generateToken(Authentication authentication) {
		UserDetails user = (UserDetails) authentication.getPrincipal();

		final Date now = new Date();
		final Date expiration = new Date(now.getTime() + this.expirationInMillis);

		return Jwts.builder()
				.issuer("Desafio jornada dev eficiente mercado livre")
				.subject(user.getUsername())
				.issuedAt(now)
				.expiration(expiration)
				.signWith(secretKey)  // Usa a nova forma de assinatura
				.compact();
	}

	public boolean isValid(String jwt) {
		try {
			Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(jwt);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public String getUserName(String jwt) {
		Claims claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(jwt)
				.getPayload();

		return claims.getSubject();
	}
}
