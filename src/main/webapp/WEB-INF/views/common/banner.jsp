<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
/* 💻 교보문고 스타일: 하단 컨텐츠(추천 도서)와 좌우 정렬선을 맞추기 위한 컨테이너 */
.main-banner-wrapper {
    width: 100%;
    max-width: 1200px; /* 추천 도서 목록의 전체 가로폭과 일치시킵니다 */
    margin: 0 auto 30px auto; /* 중앙 정렬 및 하단 여백 추가 */
    border-radius: 12px; /* 교보문고처럼 모서리를 부드럽게 라운딩 처리 */
    overflow: hidden;
    position: relative;
}

.banner-container {
    width: 100%;
    height: 350px; /* ⚙️ 교보문고 스타일의 부담 없는 적당한 배너 높이 */
    overflow: hidden;
    position: relative;
}

.banner-slide {
    display: flex;
    width: 300%; /* 이미지 3개 분량 */
    height: 100%;
    transition: transform 0.8s ease-in-out; /* 조금 더 부드러운 전환 효과 */
}

.banner-slide img {
    width: 33.3333%; /* 부모 300% 내에서 정확히 3분의 1 획득 */
    height: 350px;
    /* ✨ [비율 보존 치트키] 절대 찌그러지지 않고 지정된 공간에 맞게 원본 비율을 유지하며 채웁니다 */
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