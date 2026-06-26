package kakaoLogin;

import java.net.URLDecoder;
import java.util.Collections;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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
import member.repository.MemberDAO;
import order.model.CartVO;
import order.service.CartService;

@Controller
public class KakaoLoginController {

    @Autowired
    private CartService cartService; 
    
    @Autowired
    private MemberDAO memberDAO;

    private static final String CLIENT_ID = "6d56b80b09849d754df794b8ae017307";
    private static final String CLIENT_SECRET = "k16pKYIwdh9UfS551SqEYYYKhcefmleW";
//    private static final String REDIRECT_URI =
//    	    "https://upriver-grope-equate.ngrok-free.dev/kakao/callback";
    private static final String REDIRECT_URI =
    		"https://marina-elastic-wilder.ngrok-free.dev/kakao/callback";
    

    @GetMapping("/kakao/login")
    public String kakaoLogin() {
        String kakaoUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&response_type=code"
        		+ "&prompt=login";
        return "redirect:" + kakaoUrl;
    }

    @GetMapping("/kakao/callback")
    public String callback(@RequestParam("code") String code, 
                           HttpServletRequest request, 
                           HttpServletResponse response, 
                           HttpSession session) {
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
            
            
            MemberVO member = memberDAO.findById(username);
            if (member == null) {
                MemberVO vo = new MemberVO();
                vo.setMemberId(username);
                vo.setName(nickname);
                vo.setPassword(java.util.UUID.randomUUID().toString());
                vo.setEmail(username + "@kakao.local");
                vo.setRole("USER");

                memberDAO.signup(vo);
            }

            // 3. 세션 및 Spring Security 인증 객체 생성
            MemberVO loginUser = new MemberVO();
            loginUser.setMemberId(username);
            loginUser.setName(nickname);
            loginUser.setRole("USER");
            session.setAttribute("loginUser", loginUser);
            

            // Spring Security 컨텍스트에 강제 인증 주입
            User principal = new User(username, "", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            
            request.getSession().setAttribute(
                    "SPRING_SECURITY_CONTEXT",
                    SecurityContextHolder.getContext()
            );
            
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("guestCart".equals(cookie.getName())) {
                        try {
                            String cartJson = URLDecoder.decode(cookie.getValue(), "UTF-8");
                            System.out.println("▶ [카카오 수신 쿠키]: " + cartJson);
                            
                            // 정규식 대신 가장 직관적이고 에러 없는 중괄호 분할 기법으로 선회
                            // 예: [{"bookId":4,"count":1}] -> "bookId":4,"count":1
                            String cleanJson = cartJson.replace("[", "").replace("]", "");
                            
                            // 한 장씩 분리하기 위해 "},{" 텍스트를 기준으로 강제 분할
                            String[] items = cleanJson.split("\\},\\s*\\{");
                            
                            for (String item : items) {
                                // 찌꺼기 괄호 청소
                                String refinedItem = item.replace("{", "").replace("}", "");
                                System.out.println("▶ [안전 분해 조각]: " + refinedItem);
                                
                                String[] subParts = refinedItem.split(",");
                                int bookId = 0;
                                int count = 0;
                                
                                for(String part : subParts) {
                                    if(part.contains("bookId")) {
                                        bookId = Integer.parseInt(part.replaceAll("[^0-9]", "").trim());
                                    } else if(part.contains("count")) {
                                        count = Integer.parseInt(part.replaceAll("[^0-9]", "").trim());
                                    }
                                }
                                
                                if (bookId > 0 && count > 0) {
                                    CartVO cartVO = CartVO.builder()
                                                          .memberId(username) 
                                                          .bookId(bookId)
                                                          .count(count)
                                                          .build();
                                    
                                    boolean insertOk = cartService.addCart(cartVO); 
                                    System.out.println("▶ [DB 연동 결과]: " + (insertOk ? "성공" : "실패"));
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("❌ 카카오 쿠키 파싱 안전 레이어 예외 터짐: " + e.getMessage());
                            e.printStackTrace();
                        }
                        
                        // DB 연동 시도가 끝났으므로 브라우저 쿠키 소멸 명령
                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                        System.out.println("guestCart 쿠키 브라우저 삭제 명령");
                    }
                }
            }
           
            return "redirect:/order/cart";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/book";
        }
    }
}