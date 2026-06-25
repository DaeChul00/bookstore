<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

    <style>
        .delivery-status-box { display: flex; justify-content: space-between; align-items: center; padding: 40px 30px; background: #f9f9f9; border-radius: 15px; margin-bottom: 40px; border: 1px solid #eee; }
        .step { position: relative; text-align: center; flex: 1; }
        .circle { width: 50px; height: 50px; line-height: 50px; border-radius: 50%; background: #ddd; color: #fff; margin: 0 auto 10px; font-weight: bold; transition: all 0.3s; }
        .step.active .circle { background: #007bff; box-shadow: 0 4px 10px rgba(0,123,255,0.3); }
        .step:not(:last-child)::after { content: ''; position: absolute; top: 25px; left: 60%; width: 80%; height: 2px; background: #ddd; z-index: 0; }
        .step.active:not(:last-child)::after { background: #007bff; }
    </style>
<div class="container" style="padding: 40px 0; max-width: 800px; margin: 0 auto;">


    <h3 style="margin-bottom: 20px;">🚚 배송 조회</h3>
	<div class="delivery-status-box">
	    <div class="step" id="step1"><div class="circle">1</div><p>결제완료</p></div>
	    <div class="step" id="step2"><div class="circle">2</div><p>배송준비</p></div>
	    <div class="step" id="step3"><div class="circle">3</div><p>배송중</p></div>
	    <div class="step" id="step4"><div class="circle">4</div><p>배송완료</p></div>
	</div>
    <h3 style="margin-bottom: 20px;">📦 주문 상세 정보</h3>
    <div style="background: #fff; border: 1px solid #eee; border-radius: 12px; padding: 25px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); display: flex; align-items: center;">
        <img src="${order.bookimage}" alt="상품이미지" style="width: 100px; height: 140px; border-radius: 8px; object-fit: cover; border: 1px solid #ddd;">
        <div style="margin-left: 25px;">
            <h4 style="margin: 0 0 10px 0; color: #333;">${order.title}</h4>
            <p style="margin: 5px 0; color: #666; font-size: 14px;"><strong>수량:</strong> ${order.count}권</p>
            <p style="margin: 5px 0; color: #666; font-size: 14px;"><strong>결제금액:</strong> <fmt:formatNumber value="${order.orderPrice * order.count}" type="number"/>원</p>
            <p style="margin: 5px 0; color: #666; font-size: 14px;"><strong>주문일자:</strong> ${order.orderDate}</p>
            <p style="margin: 5px 0; color: #666; font-size: 14px;"><strong>주문번호:</strong> ${order.orderId}</p>
        </div>
    </div>

    <div style="text-align: center; margin-top: 40px;">
        <a href="${pageContext.request.contextPath}/order/list" style="padding: 10px 30px; border: 1px solid #ccc; color: #666; text-decoration: none; border-radius: 5px;">목록으로</a>
    </div>
</div>

<script>
	function updateUI(status) {
	    document.querySelectorAll('.step').forEach(el => el.classList.remove('active'));
	    if (status === '결제완료') document.getElementById('step1').classList.add('active');
	    else if (status === '배송준비') {
	        document.getElementById('step1').classList.add('active');
	        document.getElementById('step2').classList.add('active');
	    } else if (status === '배송중') {
	        document.getElementById('step1').classList.add('active');
	        document.getElementById('step2').classList.add('active');
	        document.getElementById('step3').classList.add('active');
	    } else if (status === '배송완료') {
	        document.querySelectorAll('.step').forEach(el => el.classList.add('active'));
	    }
	}
	updateUI('${order.deliveryStatus}');

    // 5초마다 실시간 확인
    setInterval(() => {
        fetch('${pageContext.request.contextPath}/order/status?orderId=${order.orderId}')
        .then(res => res.text())
        .then(status => updateUI(status.trim()));
    }, 5000);
</script>