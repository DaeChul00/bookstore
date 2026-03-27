<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="header">
	<nav class="navbar">
		<ul class="navbar_logo">
			<li><i class="fa-brands fa-accusoft"></i></li>
			<li onclick="location.href='/'">SEOIL</li>
		</ul>

		<ul class="navbar_menu">
			<li onclick="subpage('intro.html')">회사소개</li>
			<li>사업안내</li>
			<li>예약안내</li>
			<li>갤러리</li>
			<li><a href="/board/list">게시판</a></li>
		</ul>

		<ul class="navbar_icon">
			<li><i class="fa-solid fa-envelope"></i></li>
			<li><i class="fa-solid fa-camera"></i></li>
			<li><i class="fa-solid fa-user-plus"
				onclick="loadpage('login.html')"></i></li>
		</ul>

		<i class="fa-solid fa-bars"></i>

	</nav>
</div>