<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
.main-banner-wrapper {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto 30px auto;
    border-radius: 12px;
    overflow: hidden;
    position: relative;
}

.banner-container {
    width: 100%;
    height: 350px;
    overflow: hidden;
    position: relative;
}

.banner-slide {
    display: flex;
    width: 300%;
    height: 100%;
    transition: transform 0.8s ease-in-out;
}

.banner-slide img {
    width: 33.3333%;
    height: 350px;
    object-fit: cover; 
    object-position: center center;
}
</style>

<div class="main-banner-wrapper">
    <div class="banner-container">
        <div class="banner-slide" id="bannerSlide">
            <img src="${pageContext.request.contextPath}/images/banner/banner1.png" alt="배너1">
            <img src="${pageContext.request.contextPath}/images/banner/banner2.png" alt="배너2">
            <img src="${pageContext.request.contextPath}/images/banner/banner3.png" alt="배너3">
        </div>
    </div>
</div>

<script>
let current = 0;
const slide = document.getElementById("bannerSlide");

setInterval(() => {
    current++;

    if (current > 2) {
        current = 0;
    }

    // 33.3333% 단위로 정확하게 좌우 슬라이드 이동
    slide.style.transform = "translateX(-" + (current * 33.3333) + "%)";
}, 3500); // 배너를 읽을 수 있도록 3.5초 텀으로 변경
</script>