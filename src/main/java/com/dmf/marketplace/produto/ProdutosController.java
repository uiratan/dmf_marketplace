package com.dmf.marketplace.produto;

import com.dmf.marketplace.compartilhado.seguranca.UsuarioLogado;
import com.dmf.marketplace.produto.dto.NovoProdutoRequest;
import com.dmf.marketplace.produto.dto.NovoProdutoResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
public class ProdutosController {

    @PersistenceContext
    private EntityManager manager;

    @InitBinder
    public void init(WebDataBinder binder) {
        binder.addValidators(new ProibeCaracteristicaComNomeIgualValidator());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<NovoProdutoResponse> novoProduto(
            @RequestBody @Valid NovoProdutoRequest novoProdutoRequest,
            @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        Produto produto = novoProdutoRequest.toModel(manager, usuarioLogado.get());
        manager.persist(produto);
        NovoProdutoResponse novoProdutoResponse = new NovoProdutoResponse(produto);
        return ResponseEntity.ok(novoProdutoResponse);
    }

    // TODO: listar produtos de um usuario
}


/**
 * # 4. Usuário logado cadastra novo produto
 *  Aqui a gente vai permitir o cadastro de um produto por usuário logado.
 *
 *  ## Necessidades
 *  * Tem um nome
 *  * Tem um valor
 *  * Tem quantidade disponível
 *  * Características(cada produto pode ter um conjunto de características diferente)
 *  * Da uma olhada nos detalhes de produtos diferentes do mercado livre.
 *  * Tem uma descrição que vai ser feita usando Markdown
 *  * Pertence a uma categoria
 *  * Instante de cadastro
 *
 *  ## Restrições
 *  * Nome é obrigatório
 *  * Valor é obrigatório e maior que zero
 *  * Quantidade é obrigatório e >= 0
 *  * O produto possui pelo menos três características
 *  * Descrição é obrigatória e tem máximo de 1000 caracteres.
 *  * A categoria é obrigatória
 *
 *  ## Resultado esperado
 *  * Um novo produto criado e status 200 retornado
 *  * Caso dê erro de validação retorne 400 e o json dos erros
 */