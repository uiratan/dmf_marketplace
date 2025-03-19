package com.dmf.marketplace.categoria;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//2
@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final EntityManager manager;

    public CategoriaController(EntityManager manager) {
        this.manager = manager;
    }

    @PostMapping
    @Transactional
    //1
    public String criar(@RequestBody @Valid NovaCategoriaRequest request) {
        System.out.println(request);
        //1
        Categoria categoria = request.toModel(manager);
        manager.persist(categoria);
        return categoria.toString();
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        List<Categoria> categorias = manager.createQuery("SELECT c FROM Categoria c", Categoria.class).getResultList();
        return ResponseEntity.ok(categorias);
    }
}
