package com.dmf.marketplace.usuario;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> encontrarPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.delete(usuarioOpt.get());
        return ResponseEntity.noContent().build();
    }
}