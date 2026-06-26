<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
/* (스타일 그대로 유지) */
* {
	box-sizing: border-box;
	margin: 0;
	padding: 0;
}

body {
	font-family: "Noto Sans KR", "Segoe UI", sans-serif;
	background: #f4f6f8;
}

.signup-container {
	display: flex;
	justify-content: center;
	align-items: center;
	min-height: calc(100vh - 80px);
	padding: 20px;
}

.signup-box {
	width: 480px;
	background: #fff;
	padding: 40px;
	border-radius: 14px;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

.form-group {
	margin-bottom: 20px;
}

label {
	display: block;
	font-size: 13px;
	margin-bottom: 6px;
}

input, select {
	width: 100%;
	padding: 12px;
	border: 1px solid #ddd;
	border-radius: 8px;
}

.email-box {
	display: flex;
	gap: 6px;
	align-items: center;
}

.email-box input {
	width: 32%;
}

.email-box select {
	width: 32%;
}

.btn {
	border: none;
	border-radius: 8px;
	padding: 12px;
	cursor: pointer;
}

.btn.send {
	background: #4a90e2;
	color: #fff;
	flex: 1;
}

.btn.verify {
	background: #f39c12;
	color: #fff;
	width: 80px;
}

.btn.submit {
	width: 100%;
	background: #bbb;
	color: #fff;
	margin-top: 10px;
}

.msg {
	font-size: 12px;
	display: none;
}
</style>

<div class="signup-container">
	<div class="signup-box">
		<h2>Create Account</h2>
		<c:if test="${not empty msg}">
			<div style="color: red; margin-bottom: 10px;">${msg}</div>
		</c:if>

		<form id="signupForm" action="/signup" method="post">

			<div class="form-group">
				<label>아이디</label> <input type="text" name="memberId" required>
			</div>

			<div class="form-group">
				<label>비밀번호</label> <input type="password" name="password" required>
			</div>

			<div class="form-group">
				<label>이름</label> <input type="text" name="name" required>
			</div>

			<!-- 🔥 핵심: 서버로 보낼 이메일 hidden -->
			<input type="hidden" name="email" id="email">

			<div class="form-group">
				<label>이메일</label>
				<div class="email-box">
					<input type="text" id="emailId" placeholder="아이디"> <span>@</span>
					<input type="text" id="emailDomain" placeholder="도메인"> <select
						id="domainSelect">
						<option value="">직접입력</option>
						<option value="gmail.com">gmail.com</option>
						<option value="naver.com">naver.com</option>
						<option value="daum.net">daum.net</option>
					</select>

					<button type="button" id="sendBtn" class="btn send">인증</button>
				</div>
			</div>

			<div class="form-group">
				<div class="verify-box">
					<input type="text" id="authCode" placeholder="인증번호 입력">
					<button type="button" id="verifyBtn" class="btn verify">확인</button>
				</div>
				<div id="verifyMsg" class="msg"></div>
			</div>

			<button type="submit" id="signupBtn" class="btn submit" disabled>
				가입하기</button>

		</form>
	</div>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<script>
$(document).ready(function () {

    let isVerified = false;

    function getEmail() {
        return $("#emailId").val() + "@" + $("#emailDomain").val();
    }

    function isValidEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    function resetAuth() {
        isVerified = false;
        $("#signupBtn").prop("disabled", true).css("background", "#bbb");
        $("#verifyMsg").hide();
    }

    $("#domainSelect").change(function () {
        const v = $(this).val();

        if (v) {
            $("#emailDomain").val(v).prop("readonly", true);
        } else {
            $("#emailDomain").val("").prop("readonly", false);
        }

        resetAuth();
    });

    $("#emailId, #emailDomain").on("input", resetAuth);

    // 🔥 인증번호 전송
    $("#sendBtn").click(function () {

        const email = getEmail();

        if (!isValidEmail(email)) {
            alert("이메일 형식 오류");
            return;
        }

        $.post("/email/send", { email }, function () {
            alert("인증번호 발송 완료");
        });
    });

    // 🔥 인증 확인
    $("#verifyBtn").click(function () {

        const email = getEmail();

        $.post("/email/verify", {
            email: email,
            authCode: $("#authCode").val()
        }, function (res) {

            if (res === "success") {

                isVerified = true;

                $("#verifyMsg")
                    .text("인증 완료")
                    .css("color", "green")
                    .show();

                $("#signupBtn")
                    .prop("disabled", false)
                    .css("background", "#2ecc71");

            } else {

                $("#verifyMsg")
                    .text("인증 실패")
                    .css("color", "red")
                    .show();
            }
        });
    });

    // 🔥 최종 submit 직전 이메일 주입
    $("#signupForm").submit(function (e) {

        if (!isVerified) {
            e.preventDefault();
            alert("이메일 인증을 먼저 완료하세요.");
            return;
        }

        const email = getEmail().trim().toLowerCase();
        $("#email").val(email);
    });

});
</script>