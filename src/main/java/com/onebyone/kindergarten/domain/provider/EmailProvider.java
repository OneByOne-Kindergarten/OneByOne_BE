package com.onebyone.kindergarten.domain.provider;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailProvider {
    private final JavaMailSender javaMailSender;
    private final String certificationSubject = "[원바원] 인증메일입니다.";
    private final String temporaryPasswordSubject = "[원바원] 비밀번호 재설정 메일입니다.";

    public boolean sendCertifivationMail(String email, String number) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);

            String htmlContent = getCertificationMessage(number);
            messageHelper.setTo(email);
            messageHelper.setSubject(certificationSubject);
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean sendTemporaryPasswordMail(String email, String number) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);

            String htmlContent = getTemporaryPasswordMessage(number);
            messageHelper.setTo(email);
            messageHelper.setSubject(temporaryPasswordSubject);
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String getCertificationMessage(String certificationNumber) {
        StringBuilder sb = new StringBuilder();

        sb.append(
                "<div style='max-width: 600px; margin: 0 auto; padding: 40px 20px; font-family: Arial, sans-serif; background-color: #f9f9f9; border: 1px solid #ddd;'>")
                .append("<h2 style='text-align: center; color: #333;'>원바원 이메일 본인인증</h2>")
                .append("<div style='margin-top: 30px; text-align: center;'>")
                .append("<span style='display: inline-block; padding: 12px 24px; font-size: 24px; background-color: #f1f1f1; border: 1px solid #ccc; border-radius: 6px; letter-spacing: 2px;'>")
                .append(certificationNumber)
                .append("</span>")
                .append("</div>")
                .append("<p style='margin-top: 40px; text-align: center; color: #666; font-size: 14px;'>")
                .append("해당 인증번호를 인증 화면에 입력하여 주십시오.<br>")
                .append("원바원을 이용해 주셔서 감사합니다.")
                .append("</p>")
                .append("</div>");

        return sb.toString();
    }

    private String getTemporaryPasswordMessage(String temporaryPassword) {
        StringBuilder sb = new StringBuilder();

        sb.append(
                "<div style='max-width: 600px; margin: 0 auto; padding: 40px 20px; font-family: Arial, sans-serif; background-color: #f9f9f9; border: 1px solid #ddd;'>")
                .append("<h2 style='text-align: center; color: #333;'>원바원 임시 비밀번호 발급</h2>")
                .append("<div style='margin-top: 30px; text-align: center;'>")
                .append("<span style='display: inline-block; padding: 12px 24px; font-size: 24px; background-color: #f1f1f1; border: 1px solid #ccc; border-radius: 6px; letter-spacing: 2px;'>")
                .append(temporaryPassword)
                .append("</span>")
                .append("</div>")
                .append("<p style='margin-top: 40px; text-align: center; color: #666; font-size: 14px;'>")
                .append("해당 비밀번호로 로그인 후 비밀번호를 변경해주십시오.<br>")
                .append("원바원을 이용해 주셔서 감사합니다.")
                .append("</p>")
                .append("</div>");

        return sb.toString();
    }
}
