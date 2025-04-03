package com.dmf.marketplace.compra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EventosNovaCompra {

    @Autowired
    private Set<EventoCompraSucesso> eventoCompraSucesso;

    public void processa(Compra compra) {
        if (compra.processadaComSucesso()) {
            eventoCompraSucesso.forEach(evento -> evento.processa(compra));
            System.out.println("[EMAIL COMPRADOR] Pagamento aprovado! Sua compra foi confirmada.");
        } else {
            //eventosFalha
        }
    }
}
