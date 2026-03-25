<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
    /* insertform, updateform과 동일한 스타일 적용 */
    .form-container { width: 600px; margin: 40px auto; border: 1px solid #ddd; padding: 30px; border-radius: 8px; background: #fff; }
    .form-group { margin-bottom: 20px; text-align: left; }
    .form-group label { display: block; margin-bottom: 8px; font-weight: bold; color: #333; }
    .form-group input { width: 100%; padding: 10px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
    .form-group input[readonly] { background-color: #f5f5f5; cursor: not-allowed; }
    
    .btn-area { text-align: center; margin-top: 30px; display: flex; justify-content: center; gap: 10px; }
    .btn { padding: 12px 30px; cursor: pointer; border: none; border-radius: 4px; color: white; font-weight: bold; font-size: 14px; text-decoration: none; display: inline-block; }
    
    /* 교보문고 스타일 초록색 버튼 */
    .btn-submit { background-color: #005a32; }
    /* 취소 버튼 스타일 */
    .btn-cancel { background-color: #666; }
    
    h2 { text-align: center; color: #2c3e50; margin-bottom: 25px; font-weight: bold; }
</style>

<div class="form-container">
    <h2>👤 내 정보 수정</h2>
    <hr style="margin-bottom: 30px; border: 0; border-top: 1px solid #eee;">
    
    <form action="${pageContext.request.contextPath}/member/update" method="post">
        
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

        <div class="btn-area">
		    <button type="submit" class="btn btn-submit">수정 완료</button>
		    <a href="${pageContext.request.contextPath}/book/list" class="btn btn-cancel">취소</a>
		    
		    <button type="button" class="btn" 
		            style="background-color: #d9534f; margin-left: 50px;" 
		            onclick="confirmWithdraw()">회원 탈퇴</button>
		</div>
		
		<script>
		function confirmWithdraw() {
		    if (confirm("정말로 탈퇴하시겠습니까?\n탈퇴 시 모든 정보가 삭제되며 복구할 수 없습니다.")) {
		        location.href = "${pageContext.request.contextPath}/member/withdraw";
		    }
		}
		</script>
        
    </form>
</div>