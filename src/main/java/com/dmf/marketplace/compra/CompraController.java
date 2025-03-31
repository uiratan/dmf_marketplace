package com.dmf.marketplace.compra;

import com.dmf.marketplace.compartilhado.email.Emails;
import com.dmf.marketplace.compartilhado.seguranca.UsuarioLogado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("compra")
public class CompraController {

    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private Emails emails;

    @PostMapping
    @Transactional
    public ResponseEntity<Void> criar(
            @RequestBody @Valid NovaCompraRequest request,
            @AuthenticationPrincipal UsuarioLogado usuarioLogado) throws Exception {

        Compra compra = request.toModel(manager, usuarioLogado.get());
        manager.persist(compra);

        System.out.println("------ DADOS DA COMPRA GERADOS ------");
        System.out.println("Compra: " + compra.getId());
        System.out.println("Status: " + compra.getStatus());
        System.out.println("Gateway: " + compra.getGatewayPagamento());
        System.out.println("Produto: " + compra.getProduto().getNome());
        System.out.println("Quantidade: " + compra.getQuantidade());
        System.out.println("Vendedor: " + compra.getProduto().getDono().getLogin());
        System.out.println("Comprador: " + compra.getComprador().getLogin());

        emails.enviarCompra(compra);

        System.out.println("Gerando URL de pagamento para a compra: " + compra.getId());
        String urlPagamento = compra.gerarUrlPagamento();
        System.out.println("URL de pagamento gerada: " + urlPagamento);

        System.out.println("Redirecionando o comprador para a URL de pagamento: " + urlPagamento);
        System.out.println("Pagamento efetuado com sucesso!");
        System.out.println("Redirecionando o comprador para a URL do sistema: " + compra.gerarUrlRetorno());

        return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
                .location(URI.create(compra.gerarUrlRetorno()))
                .build();
    }

    @GetMapping("/sucesso/{idCompra}")
    public NovaCompraResponse success(@PathVariable UUID idCompra) {
        System.out.println("Redirecionado com sucesso para a URL do sistema: " + idCompra);
        System.out.println("Compra finalizada com sucesso!");
        NovaCompraResponse response = new NovaCompraResponse(manager.find(Compra.class, idCompra));
        return response;
    }
}

