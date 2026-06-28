<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
<link rel="stylesheet" href="/css/style.css">
<link rel="icon" href="data:;base64,iVBORw0KGgo=">

<style>
/* ===========================
   플로팅 버튼
=========================== */
#chat-float-btn{
    position:fixed;
    right:30px;
    bottom:30px;
    width:65px;
    height:65px;
    border-radius:50%;
    background:#5f6eea;
    color:#fff;
    font-size:30px;
    display:flex;
    justify-content:center;
    align-items:center;
    cursor:pointer;
    box-shadow:0 8px 25px rgba(0,0,0,.2);
    z-index:9999;
    transition:.3s;
}

#chat-float-btn:hover{
    transform:scale(1.08);
}


/* ===========================
   채팅창
=========================== */
#chat-panel{
    position:fixed;
    right:30px;
    bottom:110px;

    width:400px;
    height:450px;

    background:#f3f4f8;
    border-radius:25px;
    box-shadow:0 15px 40px rgba(0,0,0,.2);

    display:none;
    flex-direction:column;

    overflow:hidden;
    z-index:9999;
}


/* ===========================
   헤더
=========================== */
.chat-header-mini{
    flex-shrink:0;

    height:80px;
    background:#5f6eea;
    color:#fff;

    display:flex;
    align-items:center;
    justify-content:space-between;

    padding:0 25px;

    font-size:15px;
    font-weight:bold;
}

.chat-header-mini span{
    cursor:pointer;
    font-size:24px;
}


/* ===========================
   메시지 영역
=========================== */
#chatBox-mini{
    flex:1;
    min-height:0;

    overflow-y:auto;
    overflow-x:hidden;

    padding:25px;
    background:#ececef;

    display:flex;
    flex-direction:column;
}


/* ===========================
   메시지
=========================== */
.message{
    display:flex;
    flex-direction:column;
    margin-bottom:18px;
}

.mine{
    align-items:flex-end;
}

.other{
    align-items:flex-start;
}

.sender{
    font-size:13px;
    color:#666;
    margin-bottom:5px;
}

.bubble{
    max-width:70%;
    padding:14px 20px;
    border-radius:22px;
    font-size:16px;
    line-height:1.5;
    word-break:break-word;
    box-shadow:0 3px 10px rgba(0,0,0,.08);
}

.mine .bubble{
    background:#5f6eea;
    color:white;
    border-top-right-radius:8px;
}

.other .bubble{
    background:white;
    border-top-left-radius:8px;
}


/* ===========================
   입력창
=========================== */
.chat-input-mini{
    flex-shrink:0;

    height:90px;

    background:white;
    border-top:1px solid #ddd;

    padding:15px;

    display:flex;
    align-items:center;
    gap:15px;
}

.chat-input-mini input{
    flex:1;

    height:55px;

    border:1px solid #ddd;
    border-radius:35px;

    padding:0 25px;
    font-size:18px;
    outline:none;
}

.chat-input-mini button{
    width:95px;
    height:55px;

    border:none;
    border-radius:30px;

    background:#5f6eea;
    color:white;
    font-size:17px;

    cursor:pointer;
    transition:.2s;
}

.chat-input-mini button:hover{
    background:#4959db;
}
</style>

</head>

<body>
	<div class="wrapper">
		<header class="d-flex align-items-center justify-content-center">
			<jsp:include page="/WEB-INF/views/common/header.jsp" />
		</header>

		<c:if test="${showBanner}">
			<jsp:include page="/WEB-INF/views/common/banner.jsp" />
		</c:if>

		<main>
			<jsp:include page="${contentPage}" />
		</main>

		<footer class="d-flex align-items-center justify-content-center">
			<jsp:include page="/WEB-INF/views/common/footer.jsp" />
		</footer>
	</div>
	
	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/search.js"></script>
	
	<c:if test="${param.error == 'auth'}">
		<script>
			alert("관리자만 접근 가능한 페이지입니다.");
		</script>
	</c:if>
	
	
	
	
<!-- 플로팅 버튼 -->
<div id="chat-float-btn">💬</div>

<!-- 플로팅 채팅 -->
<div id="chat-panel">

    <div class="chat-header-mini">
    <div>
        💬 1:1 채팅
    </div>

    <span id="chat-close">
        <i class="fa-solid fa-xmark"></i>
    </span>
</div>

        <div id="chatBox-mini"></div>

    <div class="chat-input-mini">
        <input
            type="text"
            id="message-mini"
            placeholder="메시지를 입력하세요">

        <button
            type="button"
            onclick="sendMini()">
            전송
        </button>
    </div>

</div>

<script>
let miniSocket = null;
let miniRoomId = null;
const loginId =
    "${pageContext.request.userPrincipal.name}";


$("#chat-float-btn").click(function () {

    if ($("#chat-panel").is(":visible")) {
        $("#chat-panel").hide();
        return;
    }

    $("#chat-panel").css("display", "flex");

    if (!miniSocket ||
        miniSocket.readyState !== WebSocket.OPEN) {
        initMiniChat();
    }
});


$("#chat-close").click(function () {
    $("#chat-panel").hide();
});


function initMiniChat() {

    $.get(
        "${pageContext.request.contextPath}/chat/start",
        function(res){

            miniRoomId =
                res.roomId ? res.roomId : res;

            loadMiniMessages();
            connectMiniSocket();
        }
    );
}


function loadMiniMessages() {

    $.get(
        "${pageContext.request.contextPath}/chat/messages",
        {
            roomId: miniRoomId
        },
        function(list){

            $("#chatBox-mini").empty();

            list.forEach(function(msg){

                const type =
                    msg.sender === loginId
                    ? "mine"
                    : "other";

                $("#chatBox-mini").append(
                    "<div class='message "+type+"'>" +
                        "<div class='sender'>"
                        + msg.sender +
                        "</div>" +
                        "<div class='bubble'>"
                        + msg.message +
                        "</div>" +
                    "</div>"
                );
            });

            scrollBottom();
        }
    );
}


function connectMiniSocket() {

    miniSocket = new WebSocket(
        "ws://"
        + location.host
        + "${pageContext.request.contextPath}"
        + "/chat?roomId="
        + miniRoomId
    );

    miniSocket.onmessage = function(e){

        const data =
            JSON.parse(e.data);

        const type =
            data.sender === loginId
            ? "mine"
            : "other";

        $("#chatBox-mini").append(
            "<div class='message "+type+"'>" +
                "<div class='sender'>"
                + data.sender +
                "</div>" +
                "<div class='bubble'>"
                + data.message +
                "</div>" +
            "</div>"
        );

        scrollBottom();
    };
}


function sendMini() {

    const msg =
        $("#message-mini").val();

    if (!msg.trim()) return;

    if (!miniSocket ||
        miniSocket.readyState !== WebSocket.OPEN) {
        return;
    }

    miniSocket.send(
        JSON.stringify({
            roomId: miniRoomId,
            sender: loginId,
            message: msg
        })
    );

    $("#message-mini").val("");
}


$("#message-mini").on(
    "keydown",
    function(e){
        if (e.keyCode === 13) {
            sendMini();
        }
    }
);


function scrollBottom() {
    const box =
        $("#chatBox-mini")[0];

    box.scrollTop =
        box.scrollHeight;
}


window.addEventListener(
    "beforeunload",
    function(){
        if (miniSocket) {
            miniSocket.close();
        }
    }
);
</script>
</body>
</html>