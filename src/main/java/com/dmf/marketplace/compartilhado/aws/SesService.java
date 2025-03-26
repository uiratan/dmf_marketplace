package com.dmf.marketplace.compartilhado.aws;

import com.dmf.marketplace.compartilhado.email.MailerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@Profile("prod")
public class SesService implements MailerService {

    @Autowired
    private SesClient sesClient;

    @Value("${cloud.aws.ses.sender-email}")
    private String senderEmail;

    public void send(String destinatario, String assunto, String corpo) {
        System.out.println("Enviando email via AWS SES");

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
