package com.dmf.marketplace.compartilhado.seguranca;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserAuthenticationController {

	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private TokenManager tokenManager;
	
	private static final Logger log = LoggerFactory.getLogger(UserAuthenticationController.class);

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> authenticate(@RequestBody LoginInputDto loginInfo, HttpServletRequest request) {
	
		UsernamePasswordAuthenticationToken authenticationToken = loginInfo.build();

		try {
			Authentication authentication = authManager.authenticate(authenticationToken);
			String jwt = tokenManager.generateToken(authentication);

			// Log de sucesso com contexto
			log.info("Autenticação bem-sucedida: username={}, ip={}, endpoint={}, method={}",
					authentication.getName(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod());

			return ResponseEntity.ok(jwt);
		} catch (AuthenticationException | JOSEException e) {
			// Log de erro com contexto
			log.error("Falha na autenticação: ip={}, endpoint={}, method={}, erro={}",
					request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getMessage());

			return ResponseEntity.badRequest().build();
		}
		
	}
}
