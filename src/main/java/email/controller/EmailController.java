package email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import email.service.EmailService;

@Controller
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * 인증번호 메일 발송
     */
    @PostMapping("/send")
    @ResponseBody
    public String send(@RequestParam("email") String email) {

        try {
            System.out.println("=== 인증 메일 발송 요청 ===");
            System.out.println("받는 이메일 : " + email);

            emailService.sendAuthMail(email);

            System.out.println("메일 발송 성공");

            return "success";

        } catch (Exception e) {

            System.out.println("메일 발송 실패");
            e.printStackTrace();

            return "fail";
        }
    }

    /**
     * 인증번호 검증
     */
    @PostMapping("/verify")
    @ResponseBody
    public String verify(
            @RequestParam("email") String email,
            @RequestParam("authCode") String authCode) {

        try {
            System.out.println("=== 인증번호 검증 요청 ===");
            System.out.println("이메일 : " + email);
            System.out.println("인증번호 : " + authCode);

            boolean result =
                    emailService.verify(email, authCode);

            return result ? "success" : "fail";

        } catch (Exception e) {

            e.printStackTrace();

            return "fail";
        }
    }
    
}