<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<script src="https://js.tosspayments.com/v1/payment"></script>

<style>
.cart-container {
	width: 1000px;
	margin: 50px auto;
}

.cart-table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 20px;
}

.cart-table th, .cart-table td {
	padding: 15px;
	border-bottom: 1px solid #ddd;
	text-align: center;
}

.cart-table th {
	background-color: #2c3e50;
	color: white;
}

.book-thumb {
	width: 60px;
	height: 80px;
	object-fit: cover;
	border-radius: 4px;
}

.total-area {
	text-align: right;
	margin-top: 30px;
	font-size: 20px;
	font-weight: bold;
}

.btn-order {
	background: #e67e22;
	color: white;
	padding: 12px 30px;
	border: none;
	border-radius: 6px;
	cursor: pointer;
	font-size: 16px;
	font-weight: bold;
}

.delivery-section {
	background: #fff;
	border: 1px solid #ddd;
	border-radius: 8px;
	padding: 30px;
	margin-top: 40px;
	display: none;
}

.delivery-section h3 {
	margin-bottom: 20px;
	padding-bottom: 10px;
	border-bottom: 2px solid #2c3e50;
}

.form-group {
	margin-bottom: 15px;
	display: flex;
	align-items: center;
}

.form-group label {
	width: 120px;
	font-weight: bold;
	font-size: 14px;
}

.form-group input[type="text"] {
	padding: 10px;
	border: 1px solid #ddd;
	border-radius: 4px;
	width: 300px;
}

.address-box {
	display: flex;
	gap: 10px;
}

.address-box input {
	width: 150px !important;
}

.btn-search {
	background: #4a90e2;
	color: white;
	border: none;
	padding: 10px 15px;
	border-radius: 4px;
	cursor: pointer;
}
</style>

<div class="cart-container">
	<h2>🛒 마이 장바구니 목록</h2>

	<c:choose>
		<c:when test="${empty cartList}">
			<div style="text-align: center; padding: 50px 0; color: #777;">
				<h3>장바구니가 텅 비어 있습니다.</h3>
				<a href="${pageContext.request.contextPath}/book/list"
					style="color: #2c3e50; font-weight: bold;">책 보러 가기 ➡️</a>
			</div>
		</c:when>
		<c:otherwise>
			<table class="cart-table">
				<thead>
					<tr>
						<th>이미지</th>
						<th>도서명</th>
						<th>판매가</th>
						<th>수량</th>
						<th>합계</th>
						<th>관리</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${cartList}">
						<tr class="cart-item-row" data-bookid="${item.bookId}"
							data-title="${item.title}" data-price="${item.price}"
							data-count="${item.count}" data-image="${item.bookimage}">
							<td><img src="${item.bookimage}" class="book-thumb"></td>
							<td style="text-align: left; font-weight: bold;">${item.title}</td>
							<td><fmt:formatNumber value="${item.price}" type="number" />원</td>
							<td><input type="number" value="${item.count}" min="1"
								style="width: 50px; text-align: center;"
								onchange="handleCountChange(${item.bookId}, this.value)">
							</td>
							<td><fmt:formatNumber value="${item.price * item.count}"
									type="number" />원</td>
							<td>
								<button onclick="handleDelete(${item.bookId})"
									style="background: #d9534f; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">삭제</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<%-- 📦 배송지 정보 입력 폼 레이아웃 --%>
			<div class="delivery-section" id="deliverySection">
				<h3>📦 배송지 정보 입력</h3>
				<form id="orderForm" method="post">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}" />

					<div class="form-group">
						<label>배송지 별칭</label> <input type="text" name="addrName"
							id="addrName" placeholder="예: 집, 회사" value="기본배송지" required>
					</div>

					<div class="form-group">
						<label>우편번호</label>
						<div class="address-box">
							<input type="text" name="zipcode" id="zipcode" placeholder="우편번호"
								readonly required>
							<button type="button" class="btn-search"
								onclick="execDaumPostcode()">주소 검색</button>
						</div>
					</div>

					<div class="form-group">
						<label>배송 주소</label> <input type="text" id="roadAddress"
							placeholder="기본주소" readonly required
							style="width: 500px; margin-bottom: 5px;"><br> <input
							type="text" id="detailAddress" placeholder="상세주소 입력"
							style="width: 500px; margin-left: 120px;">
					</div>
				</form>
			</div>

			<div class="total-area">
				<button class="btn-order" id="mainOrderBtn" onclick="handleOrder()">주문하기
					➡️</button>
			</div>
		</c:otherwise>
	</c:choose>
</div>

<form id="cartActionForm" method="post" style="display: none;">
	<input type="hidden" name="${_csrf.parameterName}"
		value="${_csrf.token}" /> <input type="hidden" name="cartId"
		id="actionBookId" /> <input type="hidden" name="count"
		id="actionCount" />
</form>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script
	src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>

<script>
var isLogin = false;
<sec:authorize access="isAuthenticated()">
    isLogin = true;
</sec:authorize>

function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            let roadAddr = data.roadAddress; 
            let extraRoadAddr = ''; 

            if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                extraRoadAddr += data.bname;
            }
            if(data.buildingName !== '' && data.apartment === 'Y'){
               extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
            }
            if(extraRoadAddr !== ''){
                extraRoadAddr = ' (' + extraRoadAddr + ')';
            }

            $("#zipcode").val(data.zonecode);
            $("#roadAddress").val(roadAddr + extraRoadAddr);
            $("#detailAddress").focus();
        }
    }).open();
}

function handleCountChange(bookId, count) {
    if (count < 1) {
        alert("최소 수량은 1개입니다.");
        return;
    }

    if (isLogin) {
        var url = "${pageContext.request.contextPath}/order/updateCartAsync";
        var csrfParameterName = "${_csrf.parameterName}";
        var csrfToken = "${_csrf.token}";

        var formData = new URLSearchParams();
        formData.append("bookId", bookId);
        formData.append("count", count);    
        formData.append(csrfParameterName, csrfToken);
        fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: formData.toString()
        })
        .then(function(response) { return response.text(); })
        .then(function(data) {
            if (data === "success") { location.reload(); } 
            else { alert("수량 변경 반영에 실패했습니다."); }
        })
        .catch(function(error) {
            console.error("Error:", error);
            alert("서버 통신 중 오류가 발생했습니다.");
        });
    } else {
        updateGuestCartCookie(bookId, parseInt(count));
    }
}

function handleDelete(bookId) {
    if(confirm("해당 상품을 장바구니에서 삭제하시겠습니까?")) {
        if (isLogin) {
            var form = document.getElementById("cartActionForm");
            form.action = "${pageContext.request.contextPath}/order/deleteCart";
            document.getElementById("actionBookId").value = bookId;
            form.submit();
        } else {
            removeGuestCartCookie(bookId);
        }
    }
}

// 주소 인프라와 토스결제 모듈 최종 통합 엔진
function handleOrder() {
var deliverySec = $("#deliverySection");
    
    // 1단계: 만약 로그인한 회원이고 기본 배송지 주소가 이미 등록되어 있다면 바로 결제 처리 가동!
    // JSP EL 태그를 활용해 로그인 유저의 주소가 채워져 있는지 가드 판단
    var userZipcode = "${loginUser.zipcode}";
    var userRoadAddress = "${loginUser.roadAddress}";

    if (isLogin && userZipcode !== "" && userRoadAddress !== "") {
        // ⭕ [회원 자동 패스] 수정화면에서 등록해 둔 배송지 정보를 그대로 활용
        var paramZipcode = userZipcode.trim();
        var paramFinalAddress = userRoadAddress.trim();
        
        executePaymentProcess(paramZipcode, paramFinalAddress);
    } else {
        // ❌ [비회원 또는 주소가 없는 회원] 기존처럼 배송지 창을 열어 직접 입력을 요구
        if (deliverySec.is(":hidden")) {
            deliverySec.slideDown();
            $("#mainOrderBtn").text("토스페이로 안전 결제하기 💳");
        } else {
            if($("#zipcode").val() === "" || $("#roadAddress").val() === "") {
                alert("배송지 주소를 검색하여 입력해 주세요.");
                return;
            }
            var paramZipcode = $("#zipcode").val().trim();
            var paramFinalAddress = $("#roadAddress").val().trim() + " " + $("#detailAddress").val().trim();
            
            executePaymentProcess(paramZipcode, paramFinalAddress);
        }
    }
}

// 토스페이먼츠 및 서버 적재 통합 연동 유틸 메서드
function executePaymentProcess(paramZipcode, paramFinalAddress) {
    var items = [];
    $(".cart-item-row").each(function() {
        items.push({
            bookId: parseInt($(this).data("bookid")),
            title: $(this).data("title"),
            price: parseInt($(this).data("price")),
            count: parseInt($(this).data("count")),
            bookimage: $(this).data("image")
        });
    });

    var prepareUrl = "${pageContext.request.contextPath}/order/prepare";

    fetch(prepareUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            zipcode: paramZipcode,
            roadAddress: paramFinalAddress,
            cartItems: items 
        })
    })
    .then(response => {
        if (!response.ok) throw new Error("네트워크 응답 오류");
        return response.json();
    })
    .then(data => {
        var encodedZip = encodeURIComponent(paramZipcode);
        var encodedAddr = encodeURIComponent(paramFinalAddress);
        
        var dynamicSuccessUrl = window.location.origin + '${pageContext.request.contextPath}/order/success'
                                + '?zipcode=' + encodedZip + '&roadAddress=' + encodedAddr;
        
        const tossPayments = TossPayments("test_ck_GjLJoQ1aVZp0Bbwb0yl58w6KYe2R");
        tossPayments.requestPayment('카드', {
            amount: data.totalPrice,
            orderId: data.orderId,
            orderName: data.orderName,
            successUrl: dynamicSuccessUrl, 
            failUrl: window.location.origin + '${pageContext.request.contextPath}/order/fail'
        });
    })
    .catch(error => {
        console.error("Error:", error);
        alert("주문 준비 중 오류가 발생했습니다.");
    });
}

function getCookie(name) {
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
    return "";
}

function updateGuestCartCookie(bookId, newCount) {
    var cartCookie = getCookie("guestCart");
    var cartList = [];
    if (cartCookie) {
        try { cartList = JSON.parse(decodeURIComponent(cartCookie)); } 
        catch (e) { cartList = []; }
    }
    var isExist = false;
    for (var i = 0; i < cartList.length; i++) {
        if (cartList[i].bookId === bookId) {
            cartList[i].count = newCount;
            isExist = true;
            break;
        }
    }
    if (!isExist) { cartList.push({ bookId: bookId, count: newCount }); }
    document.cookie = "guestCart=" + encodeURIComponent(JSON.stringify(cartList)) + "; path=/; max-age=" + (30*24*60*60);
    location.reload();
}

function removeGuestCartCookie(bookId) {
    var cartCookie = getCookie("guestCart");
    if (cartCookie) {
        try {
            var cartList = JSON.parse(decodeURIComponent(cartCookie));
            cartList = cartList.filter(function(item) { return item.bookId !== bookId; });
            document.cookie = "guestCart=" + encodeURIComponent(JSON.stringify(cartList)) + "; path=/; max-age=" + (30*24*60*60);
        } catch(e) {
            document.cookie = "guestCart=; path=/; max-age=0";
        }
        location.reload();
    }
}
</script>

<c:if test="${not empty msg}">
	<script>
    alert("${msg}");
</script>
</c:if>