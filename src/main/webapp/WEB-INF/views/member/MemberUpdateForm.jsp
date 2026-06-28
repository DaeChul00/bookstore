<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
    .form-container { width: 600px; margin: 40px auto; border: 1px solid #ddd; padding: 30px; border-radius: 8px; background: #fff; }
    .form-group { margin-bottom: 20px; text-align: left; }
    .form-group label { display: block; margin-bottom: 8px; font-weight: bold; color: #333; }
    .form-group input { width: 100%; padding: 10px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
    .form-group input[readonly] { background-color: #f5f5f5; cursor: not-allowed; }
    
    /* ➕ 주소 전용 박스 레이아웃 스타일 */
    .address-box { display: flex; gap: 10px; margin-bottom: 8px; }
    .address-box input { width: 70% !important; }
    .btn-search { width: 30%; background: #4a90e2; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; }
    
    .btn-area { text-align: center; margin-top: 30px; display: flex; justify-content: center; gap: 10px; }
    .btn { padding: 12px 30px; cursor: pointer; border: none; border-radius: 4px; color: white; font-weight: bold; font-size: 14px; text-decoration: none; display: inline-block; }
    .btn-submit { background-color: #005a32; }
    .btn-cancel { background-color: #666; }
    h2 { text-align: center; color: #2c3e50; margin-bottom: 25px; font-weight: bold; }
</style>

<div class="form-container">
    <h2>👤 내 정보 수정</h2>
    <hr style="margin-bottom: 30px; border: 0; border-top: 1px solid #eee;">
    
    <form action="${pageContext.request.contextPath}/member/update" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        
        <div class="form-group">
            <label>아이디 (수정 불가)</label>
            <input type="text" name="memberId" value="${loginUser.memberId}" readonly>
        </div>

        <div class="form-group">
            <label>이름</label>
            <input type="text" name="name" value="${loginUser.name}" placeholder="변경할 이름을 입력하세요" required>
        </div>

        <div class="form-group">
            <label>이메일 주소</label>
            <input type="email" name="email" value="${loginUser.email}" placeholder="example@bookstore.com" required>
        </div>

        <div class="form-group">
            <label>우편번호</label>
            <div class="address-box">
                <input type="text" name="zipcode" id="zipcode" value="${loginUser.zipcode}" placeholder="우편번호" readonly required>
                <button type="button" class="btn-search" onclick="execDaumPostcode()">주소 검색</button>
            </div>
        </div>

        <div class="form-group">
            <label>배송 주소</label>
            <input type="text" name="roadAddress" id="roadAddress" value="${loginUser.roadAddress}" placeholder="기본주소 및 상세주소" readonly required>
        </div>

        <div class="btn-area">
            <button type="submit" class="btn btn-submit">수정 완료</button>
            <a href="${pageContext.request.contextPath}/book/list" class="btn btn-cancel">취소</a>
            
            <button type="button" class="btn" 
                    style="background-color: #d9534f; margin-left: 50px;" 
                    onclick="confirmWithdraw()">회원 탈퇴</button>
        </div>
    </form>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script>
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 도로명 주소 변수와 참고항목 변수
            let roadAddr = data.roadAddress; 
            let extraRoadAddr = ''; 

            // 법정동명이 있을 경우 추가한다. (법정리는 제외)
            // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
            if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                extraRoadAddr += data.bname;
            }
            // 건물명이 있고, 공동주택일 경우 추가한다.
            if(data.buildingName !== '' && data.apartment === 'Y'){
               extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
            }
            // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
            if(extraRoadAddr !== ''){
                extraRoadAddr = ' (' + extraRoadAddr + ')';
            }

            // ⭕ [순수 자바스크립트 매핑으로 전면 교체]
            // jQuery 충돌이나 로딩 순서 문제를 피하기 위해 ID로 직접 요소를 찾아 값을 주입합니다.
            document.getElementById('zipcode').value = data.zonecode;
            document.getElementById('roadAddress').value = roadAddr + extraRoadAddr;
            
            console.log("우편번호 주입 완료:", data.zonecode);
            console.log("도로명주소 주입 완료:", roadAddr + extraRoadAddr);
        }
    }).open();
}

function confirmWithdraw() {
    if (confirm("정말로 탈퇴하시겠습니까?\n탈퇴 시 모든 정보가 삭제되며 복구할 수 없습니다.")) {
        location.href = "${pageContext.request.contextPath}/member/withdraw";
    }
}
</script>