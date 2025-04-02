package com.dmf.marketplace.compra;

public enum StatusRetornoPagSeguro {
    SUCESSO, ERRO;

    public StatusTransacao normalizaStatus() {
        return switch (this) {
            case SUCESSO -> StatusTransacao.SUCESSO;
            case ERRO -> StatusTransacao.FALHA;
        };
    }
}
