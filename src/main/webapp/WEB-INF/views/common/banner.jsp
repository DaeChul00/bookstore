<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<style>
.banner-container {
    width: 100%;
    height: 400px;
    overflow: hidden;
}

.banner-slide {
    display: flex;
    width: 300%;
    transition: transform 1s ease;
}

.banner-slide img {
    width: 100%;
    height: 400px;
    object-fit: cover;
}
</style>

<div class="banner-container">
    <div class="banner-slide" id="bannerSlide">
        <img src="${pageContext.request.contextPath}/images/banner/banner1.png">
        <img src="${pageContext.request.contextPath}/images/banner/banner2.png">
        <img src="${pageContext.request.contextPath}/images/banner/banner3.png">
        
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

    slide.style.transform =
        "translateX(-" + (current * 33.333) + "%)";
}, 3000);
</script>	