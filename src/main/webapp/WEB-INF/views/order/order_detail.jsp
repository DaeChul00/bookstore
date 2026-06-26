<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
    /* 대철님 사이트 전용 배송 트래커 그래픽 가이드 */
    .delivery-status-box { display: flex; justify-content: space-between; align-items: center; padding: 40px 30px; background: #f9f9f9; border-radius: 15px; margin-bottom: 40px; border: 1px solid #eee; position: relative; z-index: 1; }
    .step { position: relative; text-align: center; flex: 1; z-index: 2; }
    .circle { width: 50px; height: 50px; line-height: 50px; border-radius: 50%; background: #ddd; color: #fff; margin: 0 auto 10px; font-weight: bold; transition: all 0.3s; }
    .step.active .circle { background: #005a32; box-shadow: 0 4px 10px rgba(0,90,50,0.3); } /* 대철님 시그니처 그린 컬러 테마 동기화 */
    .step:not(:last-child)::after { content: ''; position: absolute; top: 25px; left: 60%; width: 80%; height: 2px; background: #ddd; z-index: -1; }
    .step.active:not(:last-child)::after { background: #005a32; }
</style>

<div class="container bg-white p-5 rounded shadow-sm" style="margin-top: 30px; margin-bottom: 50px; max-width: 900px;">
    
    <div style="border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 30px;">
        <h2 style="margin: 0; color: #2c3e50;">🚚 실시간 배송 및 주문 상세 조회</h2>
        <p style="font-size: 13px; color: #888; margin-top: 5px;">선택하신 상품의 실시간 배송 현황과 결제 정보를 제공합니다.</p>
    </div>

    <div class="delivery-status-box">
        <div class="step" id="step1"><div class="circle">1</div><p style="font-weight:bold; margin-top:5px;">주문완료</p></div>
        <div class="step" id="step2"><div class="circle">2</div><p style="font-weight:bold; margin-top:5px;">배송준비</p></div>
        <div class="step" id="step3"><div class="circle">3</div><p style="font-weight:bold; margin-top:5px;">배송중</p></div>
        <div class="step" id="step4"><div class="circle">4</div><p style="font-weight:bold; margin-top:5px;">배송완료</p></div>
    </div>

    <h3 style="margin-bottom: 20px; color: #2c3e50;">📦 주문 상품 정보</h3>
    <div style="background: #fff; border: 1px solid #eee; border-radius: 12px; padding: 25px; box-shadow: 0 4px 10px rgba(0,0,0,0.03); display: flex; align-items: center;">
        <img src="${order.bookimage}" alt="상품이미지" style="width: 110px; height: 155px; border-radius: 8px; object-fit: cover; border: 1px solid #ddd; box-shadow: 1px 1px 5px rgba(0,0,0,0.05);">
        <div style="margin-left: 30px; flex: 1;">
            <h4 style="margin: 0 0 12px 0; color: #2c3e50; font-size: 20px; font-weight: bold;">${order.title}</h4>
            <p style="margin: 6px 0; color: #555; font-size: 14px;"><strong>주문 번호 :</strong> <span style="color:#2980b9; font-weight:bold;">${order.orderId}</span></p>
            <p style="margin: 6px 0; color: #555; font-size: 14px;"><strong>구매 수량 :</strong> ${order.count}권</p>
            <p style="margin: 6px 0; color: #555; font-size: 14px;"><strong>총 결제금액 :</strong> <span style="color: #e67e22; font-weight: bold;"><fmt:formatNumber value="${order.orderPrice}" type="number"/>원</span></p>
            <p style="margin: 6px 0; color: #777; font-size: 13px;"><strong>주문 일시 :</strong> ${order.orderDate}</p>
        </div>
    </div>

    <div style="text-align: center; margin-top: 40px;">
        <a href="${pageContext.request.contextPath}/order/list" style="padding: 12px 40px; border: 1px solid #2c3e50; color: #2c3e50; text-decoration: none; font-weight: bold; border-radius: 4px; display: inline-block; transition: 0.2s;">
            ← 주문 내역 목록으로
        </a>
    </div>
</div>

<script>
    function updateUI(status) {
        document.querySelectorAll('.step').forEach(el => el.classList.remove('active'));
        
        if (status === '주문완료' || status === '결제완료') {
            document.getElementById('step1').classList.add('active');
        } else if (status === '배송준비') {
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
    
    // 초기 로딩 시 최초 1회 즉시 실행
    updateUI('${order.deliveryStatus}');

    // 5초마다 실시간 변경 내역 비동기 리프레시
    setInterval(() => {
        fetch('${pageContext.request.contextPath}/order/status?orderId=${order.orderId}')
        .then(res => res.text())
        .then(status => {
            if(status) {
                updateUI(status.trim());
            }
        })
        .catch(err => console.error("배송 상태 동기화 실패:", err));
    }, 5000);
</script>