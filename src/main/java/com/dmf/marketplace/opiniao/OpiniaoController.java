package com.dmf.marketplace.opiniao;

import com.dmf.marketplace.compartilhado.seguranca.UsuarioLogado;
import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//4
@RestController
@RequestMapping("/produtos/{idProduto}/opiniao")
public class OpiniaoController {

    @PersistenceContext
    private EntityManager manager;

    @PostMapping
    @Transactional
    public ResponseEntity<Opiniao> criar(
            @PathVariable Long idProduto,
            @RequestBody @Valid NovaOpiniaoRequest request,
            @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        //1
        Produto produto = manager.find(Produto.class, idProduto);
        if (produto == null) {
            return ResponseEntity.notFound().build();
        }
        //1 //1
        Usuario consumidor = usuarioLogado.get();
        if (consumidor == null) {
            return ResponseEntity.notFound().build();
        }

        //1
        Opiniao opiniao = request.toModel(manager, produto, consumidor);
        manager.persist(opiniao);

        return ResponseEntity.ok(opiniao);
    }

    @GetMapping
    public ResponseEntity<List<Opiniao>> listar() {
        List<Opiniao> opinioes = manager.createQuery("SELECT o FROM Opiniao o", Opiniao.class).getResultList();
        return ResponseEntity.ok(opinioes);
    }

    @GetMapping("/{idOpiniao}")
    public ResponseEntity<Opiniao> encontrarPorId(
            @PathVariable Long idProduto,
            @PathVariable Long idOpiniao) {
        Produto produto = manager.find(Produto.class, idProduto);
        if (produto == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<Opiniao> opiniao = manager.createQuery(
                        "SELECT o FROM Opiniao o WHERE o.id = :id AND o.produto.id = :idProduto",
                        Opiniao.class)
                .setParameter("id", idOpiniao)
                .setParameter("idProduto", idProduto)
                .getResultStream()
                .findFirst();

        return opiniao.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
