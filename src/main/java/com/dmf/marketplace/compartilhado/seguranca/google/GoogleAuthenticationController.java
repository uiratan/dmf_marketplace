package com.dmf.marketplace.compartilhado.seguranca.google;

import com.dmf.marketplace.compartilhado.seguranca.TokenManager;
import com.dmf.marketplace.compartilhado.seguranca.UsersService;
import com.dmf.marketplace.usuario.SenhaLimpa;
import com.dmf.marketplace.usuario.Usuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GoogleAuthenticationController {

	private static final Logger log = LoggerFactory.getLogger(GoogleAuthenticationController.class);

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String redirectUri;

	private final UsersService usersService;
	private final TokenManager tokenManager;
	private final EntityManager manager;

	public GoogleAuthenticationController(UsersService usersService, TokenManager tokenManager, EntityManager manager) {
		this.usersService = usersService;
		this.tokenManager = tokenManager;
		this.manager = manager;
	}

	@PostMapping("/api/auth/google/callback")
	@Transactional
	public ResponseEntity<Map<String, String>> googleCallback(@RequestBody GoogleCodeDto googleCode, HttpServletRequest request) {
		try {
			log.info("Recebido código do frontend: {}", googleCode.getCode());
			log.info("Configurações injetadas: clientId={}, clientSecret={}, redirectUri={}", clientId, clientSecret, redirectUri);

			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("code", googleCode.getCode());
			params.add("client_id", clientId);
			params.add("client_secret", clientSecret);
			params.add("redirect_uri", redirectUri);
			params.add("grant_type", "authorization_code");

			log.info("Parâmetros enviados para Google: {}", params);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

			try {
				ResponseEntity<String> response = restTemplate.postForEntity(
						"https://oauth2.googleapis.com/token", requestEntity, String.class);
				log.info("Resposta completa do Google: {}", response.getBody());

				Map<String, String> tokenResponse = new ObjectMapper().readValue(response.getBody(), new TypeReference<Map<String, String>>() {});
				String idToken = tokenResponse.get("id_token");

				GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
						.setAudience(Collections.singletonList(clientId))
						.build();

				GoogleIdToken googleIdToken = verifier.verify(idToken);
				if (googleIdToken != null) {
					Payload payload = googleIdToken.getPayload();
					String email = payload.getEmail();
					log.info("Email extraído do id_token: {}", email);

					UserDetails userDetails;
					try {
						userDetails = usersService.loadUserByUsername(email);
					} catch (UsernameNotFoundException e) {
						log.info("Usuário não encontrado. Criando novo usuário com email: {}", email);
						Usuario novoUsuario = new Usuario(email, new SenhaLimpa("123456"));
//						manager.getTransaction().begin();
						manager.persist(novoUsuario);
//						manager.getTransaction().commit();
						userDetails = usersService.loadUserByUsername(email);
					}

					Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					String jwt = tokenManager.generateToken(authentication);

					log.info("Autenticação Google bem-sucedida: email={}, ip={}", email, request.getRemoteAddr());

					// Retornar o JWT em um objeto JSON
					Map<String, String> responseBody = new HashMap<>();
					responseBody.put("token", jwt);
					return ResponseEntity.ok(responseBody);
				} else {
					log.error("Token Google inválido: ip={}", request.getRemoteAddr());
					return ResponseEntity.badRequest().build();
				}
			} catch (HttpClientErrorException e) {
				log.error("Erro na requisição ao Google: status={}, resposta={}", e.getStatusCode(), e.getResponseBodyAsString());
				throw e;
			}

		} catch (Exception e) {
			log.error("Erro ao processar login Google: ip={}, erro={}", request.getRemoteAddr(), e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
}