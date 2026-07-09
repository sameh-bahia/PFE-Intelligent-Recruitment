package com.AppRecrutement.AppRecrutement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendInterviewInvitation(
            String toEmail,
            String candidatNom,
            String offreTitre,
            Date dateEntretien,
            String typeEntretien,
            String lienEntretien,
            String nomEntreprise
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("Convocation à un entretien - " + offreTitre);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm");
        String dateFormatee = dateFormat.format(dateEntretien);

        String typeLabel = typeEntretien.equals("EN_LIGNE") ? "En ligne (visioconférence)" : "Présentiel";
        String lienLabel = typeEntretien.equals("EN_LIGNE") ? "Lien de la réunion" : "Adresse";

        String htmlContent = buildInterviewEmailTemplate(
                candidatNom,
                offreTitre,
                dateFormatee,
                typeLabel,
                lienLabel,
                lienEntretien,
                nomEntreprise
        );

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String buildInterviewEmailTemplate(
            String candidatNom,
            String offreTitre,
            String dateEntretien,
            String typeEntretien,
            String lienLabel,
            String lienEntretien,
            String nomEntreprise
    ) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Convocation à un entretien</title>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                ".header h1 { margin: 0; font-size: 28px; font-weight: 600; }" +
                ".content { padding: 30px; }" +
                ".greeting { font-size: 18px; margin-bottom: 20px; }" +
                ".info-box { background-color: #f8f9fa; border-left: 4px solid #667eea; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                ".info-box p { margin: 10px 0; }" +
                ".info-label { font-weight: 600; color: #667eea; }" +
                ".info-value { color: #333; }" +
                ".footer { text-align: center; padding: 20px; color: #666; font-size: 14px; border-top: 1px solid #e0e0e0; }" +
                ".button { display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; font-weight: 600; }" +
                ".button:hover { opacity: 0.9; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🎉 Félicitations !</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p class='greeting'>Bonjour <strong>" + candidatNom + "</strong>,</p>" +
                "<p>Nous avons le plaisir de vous informer que votre candidature pour le poste de <strong>" + offreTitre + "</strong> a retenu notre attention.</p>" +
                "<p>Nous souhaitons vous rencontrer pour un entretien afin d'en discuter davantage.</p>" +
                "<div class='info-box'>" +
                "<p><span class='info-label'>📅 Date et heure :</span> <span class='info-value'>" + dateEntretien + "</span></p>" +
                "<p><span class='info-label'>📍 Type d'entretien :</span> <span class='info-value'>" + typeEntretien + "</span></p>" +
                "<p><span class='info-label'>" + (typeEntretien.contains("En ligne") ? "🔗" : "🏢") + " " + lienLabel + " :</span> <span class='info-value'><a href='" + lienEntretien + "' target='_blank' style='color: #1a73e8; text-decoration: underline;'>" + lienEntretien + "</a></span></p>" +
                "</div>" +
                (typeEntretien.contains("En ligne") ? "<p style='text-align: center;'><a href='" + lienEntretien + "' target='_blank' class='button'>🎥 Rejoindre la réunion</a></p>" : "") +
                "<p>Veuillez confirmer votre présence en répondant à cet email.</p>" +
                "<p>Nous avons hâte de vous rencontrer !</p>" +
                "<p>Cordialement,</p>" +
                "<p><strong>L'équipe de recrutement - " + nomEntreprise + "</strong></p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Ce message a été envoyé automatiquement. Merci de ne pas répondre directement à cet email.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
