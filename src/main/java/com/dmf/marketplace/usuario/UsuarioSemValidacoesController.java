package com.dmf.marketplace.usuario;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

//2
@RestController
@RequestMapping("/usuarios")
public class UsuarioSemValidacoesController {


    private final UsuarioRepository usuarioRepository;

    public UsuarioSemValidacoesController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<PagedModel<Usuario>> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Usuario> usuarioPage = usuarioRepository.findAll(pageable);

        PagedModel<Usuario> pagedModel = PagedModel.of(
                usuarioPage.getContent(),
                new PagedModel.PageMetadata(
                        usuarioPage.getSize(),
                        usuarioPage.getNumber(),
                        usuarioPage.getTotalElements(),
                        usuarioPage.getTotalPages()
                )
        );
        return ResponseEntity.ok(pagedModel);
    }


    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody AtualizaUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        usuarioRepository.findByLogin(request.login())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new RuntimeException("Login já existe: " + request.login());
                });

        usuario.atualizarUsuario(request.nome(), request.login().toLowerCase());
        usuarioRepository.save(usuario);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.delete(usuarioOpt.get());
        return ResponseEntity.noContent().build();
    }

}