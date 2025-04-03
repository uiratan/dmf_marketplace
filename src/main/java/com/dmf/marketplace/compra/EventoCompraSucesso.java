package com.dmf.marketplace.compra;

/**
 * Todo evento de compra bem-sucedida deve implementar essa interface
 */
public interface EventoCompraSucesso {

    void processa(Compra compra);

}
