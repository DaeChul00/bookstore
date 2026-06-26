package email.model;

public class EmailAuthVO {

    private String email;
    private String authCode;
    private String verified;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
}