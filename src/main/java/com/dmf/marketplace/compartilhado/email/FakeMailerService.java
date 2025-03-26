package com.dmf.marketplace.compartilhado.email;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "test"})
public class FakeMailerService implements MailerService {

    public void send(String destinatario, String assunto, String corpo) {
        System.out.println("Simulação do envio de e-mail");

        System.out.println("------ E-MAIL ENVIADO ------");
        System.out.println("Para: " + destinatario);
        System.out.println("Assunto: " + assunto);
        System.out.println(corpo);
        System.out.println("----------------------------");
    }
}
