<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container">
    <div class="signup-box" style="width: 450px; margin: 60px auto; padding: 40px; border: 1px solid #e8e8e8; background: #fff;">
        <h2 style="text-align: center; color: #2c3e50; margin-bottom: 30px;">Create Account</h2>
        
        <form action="/signup" method="post">
            <div style="margin-bottom: 15px;">
                <label style="display: block; margin-bottom: 5px; font-size: 14px; font-weight: bold;">아이디</label>
                <input type="text" name="memberId" placeholder="사용할 아이디를 입력하세요" required 
                       style="width: 100%; padding: 12px; border: 1px solid #ddd;">
            </div>

            <div style="margin-bottom: 15px;">
                <label style="display: block; margin-bottom: 5px; font-size: 14px; font-weight: bold;">비밀번호</label>
                <input type="password" name="password" placeholder="비밀번호를 입력하세요" required 
                       style="width: 100%; padding: 12px; border: 1px solid #ddd;">
            </div>

            <div style="margin-bottom: 15px;">
                <label style="display: block; margin-bottom: 5px; font-size: 14px; font-weight: bold;">이름</label>
                <input type="text" name="name" placeholder="실명을 입력하세요" required 
                       style="width: 100%; padding: 12px; border: 1px solid #ddd;">
            </div>

            <div style="margin-bottom: 25px;">
                <label style="display: block; margin-bottom: 5px; font-size: 14px; font-weight: bold;">이메일</label>
                <input type="email" name="email" placeholder="example@bookstore.com" required 
                       style="width: 100%; padding: 12px; border: 1px solid #ddd;">
            </div>

            <button type="submit" class="btn-bs" style="width: 100%; padding: 12px; font-weight: bold; background: #e67e22;">
                가입하기
            </button>
        </form>
    </div>
</div>