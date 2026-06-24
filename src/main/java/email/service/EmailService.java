package email.service;


import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import email.model.EmailAuthVO;
import email.repository.EmailDAOH2;

@Service
public class EmailService {

    @Autowired
    private EmailDAOH2 dao;

    @Autowired
    private JavaMailSender mailSender;

    public void sendAuthMail(String email) {
        try {
            Random random = new Random();

            String authCode =
                    String.valueOf(random.nextInt(900000) + 100000);

            dao.saveAuthCode(email, authCode);

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(email);
            message.setSubject("북스토어 이메일 인증");
            message.setText("인증번호는 [" + authCode + "] 입니다.");

            System.out.println("받는 이메일 : " + email);
            System.out.println("인증번호 : " + authCode);

            mailSender.send(message);

            System.out.println("메일 발송 성공");

        } catch (Exception e) {
            System.out.println("메일 발송 실패");
            e.printStackTrace();
        }
    }

    public boolean verify(String email,
                          String authCode) {

        EmailAuthVO vo =
                dao.findByEmail(email);

        if (vo == null) {
            return false;
        }

        if (!authCode.equals(vo.getAuthCode())) {
            return false;
        }

        dao.verifyEmail(email);

        return true;
    }

    public boolean isVerified(String email) {
        return dao.isVerified(email);
    }
    
    
}
