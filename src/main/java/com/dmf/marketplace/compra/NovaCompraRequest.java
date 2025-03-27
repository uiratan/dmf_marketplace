package com.dmf.marketplace.compra;

import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class NovaCompraRequest {

    @NotNull private Long idProduto;
    @NotNull @Positive private Integer quantidade;
    private GatewayPagamento gatewayPagamento;

    public NovaCompraRequest(Long idProduto, Integer quantidade, String gatewayPagamento) {
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.gatewayPagamento = GatewayPagamento.fromString(gatewayPagamento); // Conversão personalizada
    }

    public Compra toModel(EntityManager manager, Usuario comprador) throws Exception {
        Produto produto = manager.find(Produto.class, this.idProduto);
        if (produto == null) {
            throw new IllegalArgumentException("Produto nao encontrado");
        }

        produto.confereEstoque(this.quantidade);

        System.out.println("Gerando compra para o produto: " + produto.getNome());
        Compra compra = new Compra(
                this.gatewayPagamento,
                produto,
                this.quantidade,
                comprador);

        System.out.println("------ DADOS DA COMPRA GERADOS ------");
        System.out.println("Compra: " + compra.getId());
        System.out.println("Status: " + compra.getStatus());
        System.out.println("Gateway: " + compra.getGatewayPagamento());
        System.out.println("Produto: " + compra.getProduto().getNome());
        System.out.println("Quantidade: " + compra.getQuantidade());

        System.out.println("Vendedor: " + produto.getDono().getLogin());
        System.out.println("Comprador: " + comprador.getLogin());

        return compra;
    }
}
