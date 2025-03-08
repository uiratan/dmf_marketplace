package com.dmf.marketplace.usuario;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @PostMapping
    public String criar(@RequestBody @Valid NovoUsuarioRequest request) {
        return request.toString();
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