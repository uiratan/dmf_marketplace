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

        emails.enviarCompra(compra);

        String urlPagamento = compra.gerarUrlPagamento();
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
        return new NovaCompraResponse(manager.find(Compra.class, idCompra));
    }
}

