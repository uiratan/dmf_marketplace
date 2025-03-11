package com.dmf.marketplace.compartilhado.seguranca;


import com.dmf.marketplace.usuario.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioLogado implements UserDetails {

	private final Usuario usuario;
	private final User springUserDetails;

	public UsuarioLogado(Usuario usuario) {
		this.usuario = usuario;
		this.springUserDetails = new User(usuario.getLogin(), usuario.getSenha(), List.of());
	}

	public Collection<GrantedAuthority> getAuthorities() {
		return springUserDetails.getAuthorities();
	}

	public String getPassword() {
		return springUserDetails.getPassword();
	}

	public String getUsername() {
		return springUserDetails.getUsername();
	}

	public boolean isEnabled() {
		return springUserDetails.isEnabled();
	}

	public boolean isAccountNonExpired() {
		return springUserDetails.isAccountNonExpired();
	}

	public boolean isAccountNonLocked() {
		return springUserDetails.isAccountNonLocked();
	}

	public boolean isCredentialsNonExpired() {
		return springUserDetails.isCredentialsNonExpired();
	}

	public Usuario get() {
		return usuario;
	}

	@Override
	public String toString() {
		return "UsuarioLogado{" +
				"usuario=" + usuario +
				", springUserDetails=" + springUserDetails +
				'}';
	}
}
