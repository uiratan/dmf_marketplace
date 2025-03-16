package com.dmf.marketplace.categoria;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // TODO: listar categorias
}
