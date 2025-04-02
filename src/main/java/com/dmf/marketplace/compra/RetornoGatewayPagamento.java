package com.dmf.marketplace.compra;

public interface RetornoGatewayPagamento {

    /**
     *
     * @param compra compra a qual a transação pertence
     * @return uma nova transação em função do gateway de pagamento específico
     */
    Transacao toTransacao(Compra compra);
}
