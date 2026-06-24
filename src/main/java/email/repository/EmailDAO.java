package email.repository;

import email.model.EmailAuthVO;

public interface EmailDAO {

    void saveAuthCode(EmailAuthVO vo);

    EmailAuthVO findByEmail(String email);

    void verifyEmail(String email);
}
