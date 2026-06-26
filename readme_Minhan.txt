260604 변경 내용
- spring security 적용
- kakao API login 기능 추가
- 회원가입시 비밀번호 암호화하여 DB에 저장
- 세션 적용

260624 변경 내용
- KakaoLoginController REDIRECT_URI 수정 "https://upriver-grope-equate.ngrok-free.dev/kakao/callback";
- 이메일 인증 기능 추가 (email 패키지)
- CsDAOH2에서 Writer를 username로 수정
- signup.jsp 스티일 수정 및 이메일 인증용 스크립트 추가
- memeber 패키지 내에 이메일 인증을 해야만 회원가입을 할 수 있게 코드 추가

260625 변경 내용
- KakaoLoginController.java 파일을 찾기 쉽도록 패키지 새로 생성
- LoginController의 로그아웃 코드는 SecurityConfig의 logout()에서 진행되기 때문에 삭제
- 로그아웃 시 SecurituConfig.java에서 logout().deleteCookies("JSESSIONID")코드로 쿠키 삭제
- 카카오 로그인 시 header 부분에 kakao_숫자가 아닌 이름으로 보이도록 수정
- 카카오 로그인 시 h2 DB의 member 테이블에 회원 정보가 들어가도록 수정 (password는 카카오에서 제공해주지 않기 때문에 임시 데이터 삽입)
- 메인화면에 베너 추가, webapp/images/banner 폴더에 배너 이미지 넣은 후 common/banner.jsp에 코드 추가하면 됨
- 메인화면에서만 banner.jsp가 보이도록 하고 그 외의 페이지에선 보이지 않게 수정