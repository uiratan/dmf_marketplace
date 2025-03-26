package com.dmf.marketplace.compartilhado.aws;

import com.dmf.marketplace.compartilhado.email.EmailService;
import com.dmf.marketplace.pergunta.Pergunta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@Profile("prod")
public class SesService implements EmailService {

    @Autowired
    private SesClient sesClient;

    @Value("${cloud.aws.ses.sender-email}")
    private String senderEmail;

    public void enviarEmailNovaPergunta(Pergunta pergunta) {
        String destinatario = pergunta.getProduto().getDono().getLogin();

        String assunto = "Nova Pergunta sobre o produto " + pergunta.getProduto().getNome();

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

        enviarEmail(destinatario, assunto, corpo);


    }


    public void enviarEmail(String destinatario, String assunto, String corpo) {
        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .destination(Destination.builder().toAddresses(destinatario).build())
                .message(Message.builder()
                        .subject(Content.builder().data(assunto).build())
                        .body(Body.builder()
                                .html(Content.builder().data(corpo).build())
                                .build())
                        .build())
                .source(senderEmail)
                .build();

        try {
            sesClient.sendEmail(emailRequest);
            System.out.println("📧 E-mail enviado com sucesso para: " + destinatario);
        } catch (SesException e) {
            System.err.println("Falha ao enviar e-mail: " + e.getMessage());
        }
    }


}
