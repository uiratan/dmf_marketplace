package com.dmf.marketplace.usuario;

import com.dmf.marketplace.produto.Produto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        List<Usuario> usuarios = manager.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody @Valid EditaUsuarioRequest request) {
        Usuario usuario = manager.find(Usuario.class, id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        usuario.atualizarUsuario(request.nome(), request.login(), new SenhaLimpa(request.senha()));
        manager.merge(usuario);

        return ResponseEntity.ok().build();
    }
}