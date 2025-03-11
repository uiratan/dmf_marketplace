package com.dmf.marketplace.compartilhado.seguranca;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class LoginInputDto {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UsernamePasswordAuthenticationToken build() {
		return new UsernamePasswordAuthenticationToken(this.email, this.password);
	}

	@Override
	public String toString() {
		return "LoginInputDto{" +
				"email='" + email + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
