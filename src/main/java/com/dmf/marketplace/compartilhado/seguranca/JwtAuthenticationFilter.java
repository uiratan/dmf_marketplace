package com.dmf.marketplace.compartilhado.seguranca;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final TokenManager tokenManager;
	private final UsersService usersService;
	
	public JwtAuthenticationFilter(TokenManager tokenManager, UsersService usersService) {
		this.tokenManager = tokenManager;
		this.usersService = usersService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		// Ignora o filtro para requisições do H2 Console
		if (request.getRequestURI().startsWith("/h2-console")) {
			chain.doFilter(request, response);
			return;
		}

		Optional<String> possibleToken = getTokenFromRequest(request);
		
        if (possibleToken.isPresent() && tokenManager.isValid(possibleToken.get())) {
            
        	String userName = tokenManager.getUserName(possibleToken.get());
            UserDetails userDetails = usersService.loadUserByUsername(userName);
            
            UsernamePasswordAuthenticationToken authentication = 
            			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        chain.doFilter(request, response);
	}

	private Optional<String> getTokenFromRequest(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return Optional.of(authHeader.substring(7)); // Remove "Bearer "
		}
		return Optional.empty();
	}

}
