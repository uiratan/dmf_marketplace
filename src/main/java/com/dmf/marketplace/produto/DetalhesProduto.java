package com.dmf.marketplace.produto;

import com.dmf.marketplace.pergunta.Pergunta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

//5
public record DetalhesProduto(
        String decricao,
        String nome,
        BigDecimal preco,
        //1
        List<CaracteristicaResponse> caracteristicas,
        List<String> imagens,
        List<String> perguntas,
        List<Map<String, String>> opinioes,
        double mediaAvaliacao,
        Integer totalAvaliacoes
) {
    public DetalhesProduto(Produto produto) {
        this(
                produto.getDescricao(),
                produto.getNome(),
                produto.getValor(),
                //1 função como argumento
                produto.getCaracteristicas().stream()
                        .map(CaracteristicaResponse::new)
                        .toList(),
                produto.getImagens(),
                //1 //1
                produto.getPerguntas().stream().map(Pergunta::getTitulo).toList(),
                //1
                produto.getOpinioes().stream().map(
                        opiniao -> Map.of(
                                "titulo", String.valueOf(opiniao.getTitulo()),
                                "descricao", opiniao.getDescricao())
                        ).toList(),
                produto.getMediaNotas(),
                produto.getOpinioes().size()
        );
    }
}

record CaracteristicaResponse(String nome, String descricao) {
    public CaracteristicaResponse(CaracteristicaProduto caracteristica) {
        this(caracteristica.getNome(), caracteristica.getDescricao());
    }
}
