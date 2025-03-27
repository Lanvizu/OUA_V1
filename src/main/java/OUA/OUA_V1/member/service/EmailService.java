package OUA.OUA_V1.member.service;

import OUA.OUA_V1.member.exception.MemberEmailCreateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private static final String senderEmail= "ghzm888@gmail.com";

    public void sendVerificationEmail(String email, String code){
        MimeMessage message = createMail(email, code);
        mailSender.send(message);
    }

    private MimeMessage createMail(String email, String code){

        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + code + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            // 예외처리 개선 필요
            throw new MemberEmailCreateException();
        }
        return message;
    }
}
