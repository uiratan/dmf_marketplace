package com.dmf.marketplace.compra;

import com.dmf.marketplace.compartilhado.email.Emails;
import com.dmf.marketplace.compartilhado.seguranca.UsuarioLogado;
import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("compra")
public class FechaCompraParte1Controller {

    @PersistenceContext
    private EntityManager manager;

    //1
    @Autowired
    private Emails emails;

    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, String>> criar(
            //1
            @RequestBody @Valid NovaCompraRequest request,
            //1
            @AuthenticationPrincipal UsuarioLogado usuarioLogado,
            UriComponentsBuilder uriComponentsBuilder) throws Exception {

        //1
        Produto produtoASerComprado = manager.find(Produto.class, request.getIdProduto());
        if (produtoASerComprado == null) {
            throw new IllegalArgumentException("Produto nao encontrado");
        }

        Integer quantidade = request.getQuantidade();
        boolean abateu = produtoASerComprado.abaterEstoque(quantidade);

        //1
        if (abateu) {
            //1
            Usuario comprador = usuarioLogado.get();
            //1
            GatewayPagamento gatewayPagamento = request.getGatewayPagamento();
            //1
            Compra compra = new Compra(
                    produtoASerComprado,
                    quantidade,
                    comprador,
                    gatewayPagamento);
            manager.persist(compra);

            emails.novaCompra(compra);

            Map<String, String> jsonRetorno = Map.of(
                    "id", compra.getId().toString(),
                    "status", compra.getStatus().name(),
                    "gatewayPagamento", compra.getGatewayPagamento().name(),
                    "produto", compra.getProduto().getNome(),
                    "quantidade", String.valueOf(compra.getQuantidade()),
                    "comprador", compra.getComprador().getLogin(),
                    "urlRedirecionamento", compra.urlRedirecionamento(uriComponentsBuilder)
            );

            return ResponseEntity.ok(jsonRetorno);
        }

        BindException problemaComEstoque = new BindException(request, "novaCompraRequest");
        problemaComEstoque.reject(null, "Não foi possível realizar a compra por conta do estoque");

        throw problemaComEstoque;
    }

}

