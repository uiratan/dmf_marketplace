package com.dmf.marketplace.usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

//2
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @PersistenceContext
    private EntityManager manager;

    //1
    @Autowired
    private ProibeUsuarioComEmailDuplicadoValidator  proibeUsuarioComEmailDuplicadoValidator;

    @InitBinder
    public void init(WebDataBinder binder) {
        binder.addValidators(proibeUsuarioComEmailDuplicadoValidator );
    }

    @PostMapping
    @Transactional
    //1
    public void criar(@RequestBody @Valid NovoUsuarioRequest request) {
        Usuario usuario = request.toModel();
        manager.persist(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> findById(@PathVariable Long id) {
        Usuario usuario = manager.find(Usuario.class, id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario.toString());
    }

}