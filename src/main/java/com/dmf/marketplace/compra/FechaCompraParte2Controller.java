package com.dmf.marketplace.compra;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class FechaCompraParte2Controller {

    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private EventosNovaCompra eventosNovaCompra;

    @PostMapping("/retorno-pagseguro/{idCompra}")
    @Transactional
    public ResponseEntity<Compra> processamentoPagSeguro(
            @PathVariable("idCompra") UUID idCompra,
            @RequestBody @Valid RetornoPagSeguroRequest request
    ) {
        return processaTransacao(idCompra, request);
    }

    @PostMapping("/retorno-paypal/{idCompra}")
    @Transactional
    public ResponseEntity<Compra> processamentoPaypal(
            @PathVariable("idCompra") UUID idCompra,
            @RequestBody @Valid RetornoPagpalRequest request
    ) {
        return processaTransacao(idCompra, request);
    }

    private ResponseEntity<Compra> processaTransacao(
            UUID idCompra,
            RetornoGatewayPagamento request) {
        Compra compra = manager.find(Compra.class, idCompra);
        compra.adicionaTransacao(request);
        manager.merge(compra);

        eventosNovaCompra.processa(compra);

        return ResponseEntity.ok(compra);
    }

}

