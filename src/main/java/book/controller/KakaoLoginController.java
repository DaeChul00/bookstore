package book.controller;

import java.util.Collections;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import member.model.MemberVO;

@Controller
public class KakaoLoginController {

    private static final String CLIENT_ID = "6d56b80b09849d754df794b8ae017307";
    private static final String CLIENT_SECRET = "k16pKYIwdh9UfS551SqEYYYKhcefmleW";
    private static final String REDIRECT_URI = "http://localhost:8888/kakao/callback";

    @GetMapping("/kakao/login")
    public String kakaoLogin() {
        String kakaoUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&response_type=code";
        return "redirect:" + kakaoUrl;
    }

    @GetMapping("/kakao/callback")
    public String callback(@RequestParam("code") String code, HttpSession session) {
        try {
            RestTemplate rt = new RestTemplate();

            // 1. 토큰 발급 요청
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", CLIENT_ID);
            params.add("client_secret", CLIENT_SECRET);
            params.add("redirect_uri", REDIRECT_URI);
            params.add("code", code);

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
            ResponseEntity<String> tokenResponse = rt.postForEntity("https://kauth.kakao.com/oauth/token", tokenRequest, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode tokenNode = mapper.readTree(tokenResponse.getBody());
            String accessToken = tokenNode.get("access_token").asText();

            // 2. 카카오 사용자 정보 조회
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);
            HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);
            ResponseEntity<String> userResponse = rt.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, userRequest, String.class);

            JsonNode userNode = mapper.readTree(userResponse.getBody());
            Long kakaoId = userNode.get("id").asLong();
            String nickname = userNode.get("properties").get("nickname").asText();
            String username = "kakao_" + kakaoId;

            // 3. 세션 및 Spring Security 인증 객체 생성
            MemberVO loginUser = new MemberVO();
            loginUser.setMemberId(username);
            loginUser.setName(nickname);
            loginUser.setRole("USER");
            session.setAttribute("loginUser", loginUser);

            // [핵심] Spring Security 컨텍스트에 강제 인증 주입
            // 이 처리가 있어야 <sec:authentication> 태그가 로그인 상태를 인식합니다.
            User principal = new User(username, "", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            return "redirect:/book";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/book";
        }
    }
}