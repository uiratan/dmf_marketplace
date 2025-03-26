package com.dmf.marketplace.compartilhado.email;

public interface MailerService {

    void send(String destinatario, String assunto, String corpoHtml);
}
