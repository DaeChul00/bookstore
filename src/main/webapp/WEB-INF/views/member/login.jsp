<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container">
    <div class="login-box" style="width: 400px; margin: 80px auto; padding: 40px; border: 1px solid #e8e8e8; background: #fff;">
        <h2 style="text-align: center; color: #2c3e50; margin-bottom: 30px;">BOOKSTORE Login</h2>
        
        <form action="/login" method="post">
            
            <%-- 서버에서 로그인 실패 시 보낸 error 파라미터가 있을 때만 출력 --%>
            <c:if test="${param.error == 'true'}">
                <p style="color: #e74c3c; font-size: 13px; text-align: center; margin-bottom: 15px;">
                    아이디 또는 비밀번호를 확인해주세요.
                </p>
            </c:if>

            <div style="margin-bottom: 15px;">
                <input type="text" name="memberId" placeholder="아이디" required 
                       style="width: 100%; padding: 12px; border: 1px solid #ddd; outline: none;">
            </div>
            
            <div style="margin-bottom: 20px;">
                <input type="password" name="password" placeholder="비밀번호" required 
                       style="width: 100%; padding: 12px; border: 1px solid #ddd; outline: none;">
            </div>
            
            <button type="submit" class="btn-bs" style="width: 100%; padding: 12px; font-weight: bold;">
                로그인
            </button>
        </form>

        <div style="margin-top: 20px; text-align: center; font-size: 14px; color: #666;">
            아직 회원이 아니신가요? <a href="/signup" style="color: #e67e22; font-weight: bold;">회원가입</a>
        </div>
    </div>
</div>