package com.dmf.marketplace.usuario;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

//2
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {


    private final UsuarioRepository usuarioRepository;

    //1
    @Autowired
    private ProibeUsuarioComEmailDuplicadoValidator proibeUsuarioComEmailDuplicadoValidator;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @InitBinder
    public void init(WebDataBinder binder) {
        binder.addValidators(proibeUsuarioComEmailDuplicadoValidator);

    }

    @PostMapping
    @Transactional
    //1
    public void criar(@RequestBody @Valid NovoUsuarioRequest request) {
        if (usuarioRepository.findByLogin(request.login().toLowerCase()).isPresent()) {
            throw new RuntimeException("Login já existe: " + request.login());
        }

        Usuario usuario = request.toModel();
        usuarioRepository.save(usuario);
    }
}