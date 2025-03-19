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

import java.util.List;

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

    @GetMapping("{id}")
    public ResponseEntity<Produto> listaProduto(@PathVariable Long id) {
        Produto produto = manager.find(Produto.class, id);
        return ResponseEntity.ok(produto);
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listar() {
        List<Produto> produtos = manager.createQuery("SELECT p FROM Produto p", Produto.class).getResultList();
        return ResponseEntity.ok(produtos);
    }
}