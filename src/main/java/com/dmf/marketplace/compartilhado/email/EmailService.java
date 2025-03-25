package com.dmf.marketplace.compartilhado.email;

import com.dmf.marketplace.pergunta.Pergunta;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void enviarEmailNovaPergunta(Pergunta pergunta) {
        String destinatario = pergunta.getProduto().getUsuario().getLogin();
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
                destinatario,
                pergunta.getProduto().getNome(),
                pergunta.getPergunta(),
                pergunta.getProduto().getId(),
                pergunta.getId()
        );

        // Simulação do envio de e-mail
        System.out.println("------ E-MAIL ENVIADO ------");
        System.out.println("Para: " + destinatario);
        System.out.println("Assunto: " + assunto);
        System.out.println(corpo);
        System.out.println("----------------------------");
    }
}
