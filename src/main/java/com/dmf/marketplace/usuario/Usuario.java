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
    @Column(name = "login", nullable = false)
    private String login;
    @Column(name = "senha", nullable = false)
    private String senha;
    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    //1
    public Usuario(String login, SenhaLimpa senha) {
        Assert.hasText(login, "login é obrigatório");
        Assert.notNull(senha, "o objeto do tipo SenhaLimpa não pode ser nulo");

        this.login = login;
        this.senha = senha.hash();
        this.createdAt = Instant.now();
    }

    @Deprecated
    public Usuario() {
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public boolean verificarSenha(String senha) {
        return new BCryptPasswordEncoder().matches(senha, this.senha);
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
