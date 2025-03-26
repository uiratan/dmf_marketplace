package com.dmf.marketplace.pergunta;

import com.dmf.marketplace.compartilhado.email.EmailService;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PerguntaListener {

    @Autowired
    private EmailService emailService;

    @PostPersist
    public void enviarEmailNovaPergunta(Pergunta pergunta) {
        enviarPerguntaPorEmail(pergunta);
    }

    private void enviarPerguntaPorEmail(Pergunta pergunta) {
        System.out.println("Executando PerguntaListener");

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

        emailService.enviarEmail(destinatarioEmail, assunto, corpo);
    }
}
