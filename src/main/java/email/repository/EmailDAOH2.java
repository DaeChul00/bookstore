package email.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import email.model.EmailAuthVO;

@Repository
public class EmailDAOH2 {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveAuthCode(String email, String authCode) {

        String sql =
                "MERGE INTO email_auth(email, auth_code, verified) " +
                "KEY(email) VALUES (?, ?, 'N')";

        jdbcTemplate.update(sql, email, authCode);
    }

    public EmailAuthVO findByEmail(String email) {

        String sql =
                "SELECT * FROM email_auth WHERE email=?";

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> {
                        EmailAuthVO vo = new EmailAuthVO();
                        vo.setEmail(rs.getString("email"));
                        vo.setAuthCode(rs.getString("auth_code"));
                        vo.setVerified(rs.getString("verified"));
                        return vo;
                    },
                    email
            );
        }
        catch (Exception e) {
            return null;
        }
    }

    public void verifyEmail(String email) {

        String sql =
                "UPDATE email_auth SET verified='Y' WHERE email=?";

        jdbcTemplate.update(sql, email);
    }

    public boolean isVerified(String email) {

        String sql =
                "SELECT COUNT(*) FROM email_auth " +
                "WHERE email=? AND verified='Y'";

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        email
                );

        return count != null && count > 0;
    }
}