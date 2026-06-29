<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
    .room-wrapper {
        width: 900px;
        margin: 40px auto;
    }

    .room-title {
        font-size: 24px;
        font-weight: bold;
        margin-bottom: 20px;
    }

    .room-card {
        background: #fff;
        border: 1px solid #e5e5e5;
        border-radius: 12px;
        padding: 18px 20px;
        margin-bottom: 12px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        transition: 0.2s;
    }

    .room-card:hover {
        box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        transform: translateY(-2px);
    }

    .room-info {
        display: flex;
        flex-direction: column;
        gap: 6px;
    }

    .room-users {
        font-weight: 600;
        color: #333;
    }

    .room-meta {
        font-size: 13px;
        color: #888;
    }

    .room-message {
        font-size: 14px;
        color: #555;
        margin-top: 4px;
    }

    .room-right {
        text-align: right;
        min-width: 140px;
    }

    .enter-btn {
        display: inline-block;
        padding: 8px 14px;
        background: #4e73df;
        color: white;
        border-radius: 8px;
        text-decoration: none;
        font-size: 13px;
    }

    .enter-btn:hover {
        background: #365fc7;
    }

    .empty {
        text-align: center;
        color: #888;
        padding: 40px 0;
    }
</style>

<script>
function loadRoomList(){

    $.ajax({

        url:"${pageContext.request.contextPath}/admin/roomListAjax",

        type:"GET",

        success:function(result){

            $("#room-wrapper").html(result);

        }

    });

}

setInterval(loadRoomList,3000);
</script>

<div id="room-wrapper" class="room-wrapper">

    <div class="room-title">💬 1:1 문의 채팅방</div>

    <c:if test="${empty roomList}">
        <div class="empty">생성된 채팅방이 없습니다.</div>
    </c:if>

    <jsp:include page="roomListBody.jsp"/>

</div>