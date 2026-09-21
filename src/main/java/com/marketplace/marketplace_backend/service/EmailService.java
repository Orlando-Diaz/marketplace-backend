package com.marketplace.marketplace_backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarBienvenida(String destinatario, String nombre) {
        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto; padding: 24px; background-color: #f4f4f7;">
                  <div style="background-color: #ffffff; border-radius: 8px; padding: 32px; text-align: center;">
                    <h1 style="color: #2563eb; margin-bottom: 8px;">🛍️ Marketplace</h1>
                    <h2 style="color: #111827;">¡Bienvenido, %s!</h2>
                    <p style="color: #4b5563; font-size: 15px; line-height: 1.5;">
                      Tu cuenta ha sido creada exitosamente. Ya puedes empezar a explorar
                      el catálogo, publicar tus propios productos y realizar tu primera compra.
                    </p>
                    <div style="margin: 24px 0;">
                      <span style="background-color: #2563eb; color: white; padding: 10px 24px; border-radius: 6px; font-weight: bold;">
                        ¡Gracias por unirte!
                      </span>
                    </div>
                    <p style="color: #9ca3af; font-size: 12px; margin-top: 24px;">
                      Este es un correo automático, no respondas a este mensaje.
                    </p>
                  </div>
                </div>
                """.formatted(nombre);

        enviarHtml(destinatario, "¡Bienvenido a Marketplace!", html);
    }

    public void enviarConfirmacionCompra(String destinatario, String nombre, Long ordenId, String total) {
        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto; padding: 24px; background-color: #f4f4f7;">
                  <div style="background-color: #ffffff; border-radius: 8px; padding: 32px;">
                    <h1 style="color: #16a34a; text-align: center;">✅ Compra confirmada</h1>
                    <p style="color: #4b5563; font-size: 15px;">Hola %s,</p>
                    <p style="color: #4b5563; font-size: 15px; line-height: 1.5;">
                      Tu compra ha sido procesada exitosamente. Aquí el resumen:
                    </p>
                    <table style="width: 100%%; border-collapse: collapse; margin: 16px 0;">
                      <tr>
                        <td style="padding: 8px 0; color: #6b7280;">Número de orden</td>
                        <td style="padding: 8px 0; text-align: right; font-weight: bold; color: #111827;">#%d</td>
                      </tr>
                      <tr style="border-top: 1px solid #e5e7eb;">
                        <td style="padding: 8px 0; color: #6b7280;">Total pagado</td>
                        <td style="padding: 8px 0; text-align: right; font-weight: bold; color: #16a34a; font-size: 18px;">$%s</td>
                      </tr>
                    </table>
                    <p style="color: #9ca3af; font-size: 12px; margin-top: 24px;">
                      Gracias por tu compra. Este es un correo automático, no respondas a este mensaje.
                    </p>
                  </div>
                </div>
                """.formatted(nombre, ordenId, total);

        enviarHtml(destinatario, "Confirmación de compra #" + ordenId, html);
    }

    private void enviarHtml(String destinatario, String asunto, String html) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(html, true);
            mailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage());
        }
    }
}