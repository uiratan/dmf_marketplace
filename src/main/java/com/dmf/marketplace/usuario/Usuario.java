package com.dmf.marketplace.usuario;

import com.dmf.marketplace.compartilhado.ExcludeFromJacocoGeneratedReport;
import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Objects;

//1
@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue
    @Column(name = "id_usuario")
    private Long id;
    @Column(name = "nome")
    private String nome;
    @Column(name = "login", nullable = false)
    private String login;
    @Column(name = "senha", nullable = false)
    private String senha;
    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    //1
    public Usuario(String login, SenhaLimpa senhaLimpa) {
        Assert.hasText(login, "login é obrigatório");
        Assert.notNull(senhaLimpa, "o objeto do tipo SenhaLimpa não pode ser nulo");

        this.login = login;
        this.senha = senhaLimpa.hash();
        this.createdAt = Instant.now();
    }

    @Deprecated
    public Usuario() {
    }

    public void atualizarUsuario(String nome, String login) {
        this.nome = nome;
        this.login = login;
        updatedAt = Instant.now();
//        this.senha = senhaLimpa.hash();
    }

    public boolean verificarSenha(String senha) {
        return new BCryptPasswordEncoder().matches(senha, this.senha);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @ExcludeFromJacocoGeneratedReport
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
