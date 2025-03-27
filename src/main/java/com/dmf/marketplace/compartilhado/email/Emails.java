package com.dmf.marketplace.compartilhado.email;

import com.dmf.marketplace.compra.Compra;
import com.dmf.marketplace.compra.NovaCompraRequest;
import com.dmf.marketplace.pergunta.Pergunta;
import com.dmf.marketplace.produto.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Emails {

    @Autowired
    private MailerService mailerService;

    public void enviarPerguntaPorEmail(Pergunta pergunta) {
        System.out.println("Enviando Email");

        String destinatarioEmail = pergunta.getProduto().getDono().getLogin();
        String destinatarioNome = pergunta.getProduto().getDono().getLogin(); // atualizar para nome quando implementar
        String assunto = "Nova pergunta sobre seu produto: " + pergunta.getProduto().getNome();
        String corpo = """
                Olá %s,

                Você recebeu uma nova pergunta sobre seu produto "%s":

                Pergunta: "%s"

                Acesse o link abaixo para visualizar e responder a pergunta:
                http://localhost:8080/produtos/%d/perguntas/%d

                Atenciosamente,
                Equipe Marketplace
                """.formatted(
                destinatarioNome,
                pergunta.getProduto().getNome(),
                pergunta.getTitulo(),
                pergunta.getProduto().getId(),
                pergunta.getId()
        );

        mailerService.send(destinatarioEmail, assunto, corpo);
    }

    public void enviarCompra(Compra compra) {
        System.out.println("Enviando Email");

        String destinatarioEmail = compra.getProduto().getDono().getLogin();
        String destinatarioNome = compra.getProduto().getDono().getLogin();
        String assunto = "Uma compra foi registrada para o seu produto!";
        String corpo = """
                Olá %s,

                Seu produto foi vendido com sucesso!

                Detalhes da compra:
                Produto: %s
                Quantidade: %d
                Valor total: R$ %.2f

                Atenciosamente,
                Equipe Marketplace
                """.formatted(
                destinatarioNome,
                compra.getProduto().getNome(),
                compra.getQuantidade(),
                compra.getQuantidade() * compra.getProduto().getValor().doubleValue()
        );

        mailerService.send(destinatarioEmail, assunto, corpo);
    }
}
