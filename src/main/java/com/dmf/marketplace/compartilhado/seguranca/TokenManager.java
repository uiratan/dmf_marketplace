package com.dmf.marketplace.compartilhado.seguranca;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenManager {

	private final String secret;
	private final long expirationInMillis;
	private final JWK jwk;

	public TokenManager(@Value("${jwt.secret:}") String aSecret, // Valor padrão vazio
						@Value("${jwt.expiration}") long expirationInMillis) {
		this.expirationInMillis = expirationInMillis;

		// Se o secret não for fornecido ou for muito curto, gere um novo
		if (!StringUtils.hasText(aSecret) || aSecret.length() < 32) { // 32 caracteres é um mínimo razoável para Base64
			this.secret = generateSecureSecret();
		} else {
			this.secret = aSecret;
		}
		this.jwk = new OctetSequenceKey.Builder(this.secret.getBytes())
				.algorithm(JWSAlgorithm.HS256)
				.build();
	}

	private String generateSecureSecret() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] secretBytes = new byte[32]; // 256 bits
		secureRandom.nextBytes(secretBytes);
		return Base64.getEncoder().encodeToString(secretBytes); // Codifica em Base64 para facilitar uso
	}

	public String generateToken(Authentication authentication) throws JOSEException {
		UserDetails user = (UserDetails) authentication.getPrincipal();

		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.issuer("Desafio jornada dev eficiente mercado livre")
				.subject(user.getUsername())
				.issueTime(new Date())
				.expirationTime(new Date(System.currentTimeMillis() + expirationInMillis))
				.build();

		SignedJWT signedJWT = new SignedJWT(
				new com.nimbusds.jose.JWSHeader(JWSAlgorithm.HS256),
				claimsSet
		);

		// Cast para OctetSequenceKey para acessar toByteArray()
		signedJWT.sign(new MACSigner(((OctetSequenceKey) jwk).toByteArray()));

		return signedJWT.serialize();
	}

	public boolean isValid(String token) {
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);
			return signedJWT.verify(new MACVerifier(((OctetSequenceKey) jwk).toByteArray())) &&
					new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime());
		} catch (ParseException | JOSEException e) {
			return false;
		}
	}

	public String getUserName(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
