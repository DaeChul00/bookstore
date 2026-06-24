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