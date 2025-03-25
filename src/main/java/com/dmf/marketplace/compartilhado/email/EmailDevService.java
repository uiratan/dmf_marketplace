package com.dmf.marketplace.compartilhado.email;

import com.dmf.marketplace.pergunta.Pergunta;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "test"})
public class EmailDevService implements EmailService {

    public void enviarEmail(String destinatario, String assunto, String corpo) {
        // Simulação do envio de e-mail
        System.out.println("------ E-MAIL ENVIADO ------");
        System.out.println("Para: " + destinatario);
        System.out.println("Assunto: " + assunto);
        System.out.println(corpo);
        System.out.println("----------------------------");
    }
}
