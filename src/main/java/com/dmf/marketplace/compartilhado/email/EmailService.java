package com.dmf.marketplace.compartilhado.email;

public interface EmailService {

    void enviarEmail(String destinatario, String assunto, String corpoHtml);
}
