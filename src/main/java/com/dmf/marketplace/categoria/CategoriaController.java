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
}

/**
 * necessidades
 * No mercado livre você pode criar hierarquias de categorias livres.
 * Ex: Tecnologia -> Celulares -> Smartphones -> Android,Ios etc.
 * Perceba que o sistema precisa ser flexível o suficiente para que essas sequências sejam criadas.
 *
 * Toda categoria tem um nome
 * A categoria pode ter uma categoria mãe
 *
 * restrições
 * O nome da categoria é obrigatório
 * O nome da categoria precisa ser único
 *
 * resultado esperado
 * categoria criada e status 200 retornado pelo endpoint.
 * caso exista erros de validação, o endpoint deve retornar 400 e o json dos erros.
 */