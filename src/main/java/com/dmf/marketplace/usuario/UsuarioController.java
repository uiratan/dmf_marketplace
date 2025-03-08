package com.dmf.marketplace.usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

//1
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @PersistenceContext
    private EntityManager manager;

    @PostMapping
    @Transactional
    //1
    public void criar(@RequestBody @Valid NovoUsuarioRequest request) {
        Usuario usuario = request.toModel();
        manager.persist(usuario);
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id) {
        return manager.find(Usuario.class, id).toString();
    }

}

/**
 * Cadastro de novo usuário
 * necessidades
 * precisamos saber o instante do cadastro, login e senha.
 * restrições
 * O instante não pode ser nulo e não pode ser no futuro
 * O login não pode ser em branco ou nula
 * O login precisa ter o formato do email
 * A senha não pode ser branca ou nula
 * A senha precisa ter no mínimo 6 caracteres
 * A senha deve ser guardada usando algum algoritmo de hash da sua escolha.
 * resultado esperado
 * O usuário precisa estar criado no sistema
 * O cliente que fez a requisição precisa saber que o usuário foi criado. Apenas um retorno com status 200 está suficente.
 */