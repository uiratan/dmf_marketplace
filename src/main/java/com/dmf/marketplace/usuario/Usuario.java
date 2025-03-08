package com.dmf.marketplace.usuario;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_usuario")
public class Usuario {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue
    @Column(name = "id_usuario")
    private Long id;
    @Column(name = "login", nullable = false)
    private String login;
    @Column(name = "senha", nullable = false)
    private String senha;
    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    public Usuario(String login, String senha) {
        Assert.hasText(login, "login é obrigatório");
        Assert.hasText(senha, "senha é obrigatória");

        this.login = login;
        this.setSenha(senha); // Usa o setter para aplicar o hash
        this.createdAt = Instant.now();

        Assert.isTrue(createdAt != null, "data de criação não pode ser nula");
        Assert.isTrue(Instant.now().isAfter(createdAt), "data de criação não pode ser nula");
    }

    @Deprecated
    public Usuario() {
    }

    public boolean verificarSenha(String senha) {
        return passwordEncoder.matches(senha, this.senha);
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    private void setSenha(String senha) {
        Assert.hasText(senha, "senha é obrigatória");
        this.senha = passwordEncoder.encode(senha);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", senha='" + senha + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }


}
