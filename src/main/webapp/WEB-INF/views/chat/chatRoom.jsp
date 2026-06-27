<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<div class="chat-wrapper">

    <div class="chat-header">
    <div>
        <i class="fa-solid fa-headset"></i>
        관리자 상담센터
    </div>
</div>

    <div id="chatBox">
        <c:forEach var="msg" items="${messages}">
            <div class="message ${msg.sender == pageContext.request.userPrincipal.name ? 'mine' : 'other'}">
                <div class="sender">${msg.sender}</div>
                <div class="bubble">${msg.message}</div>
            </div>
        </c:forEach>
    </div>

    <div class="chat-input">
        <input type="text" id="message" placeholder="메시지를 입력하세요">
        <button type="button" onclick="sendMessage()">전송</button>
    </div>

</div>

<script>
let socket = null;
const roomId = "${roomId}";
const loginId = "${pageContext.request.userPrincipal.name}";

function connect() {

    socket = new WebSocket(
        "ws://" + location.host + "${pageContext.request.contextPath}" + "/chat?roomId=" + roomId
    );

    socket.onmessage = function (e) {

        const data = JSON.parse(e.data);
        const type = (data.sender === loginId) ? "mine" : "other";

        $("#chatBox").append(
            "<div class='message " + type + "'>" +
                "<div class='sender'>" + data.sender + "</div>" +
                "<div class='bubble'>" + data.message + "</div>" +
            "</div>"
        );

        $("#chatBox").scrollTop($("#chatBox")[0].scrollHeight);
    };
}

function sendMessage() {

    const msg = $("#message").val();
    if (!msg.trim()) return;

    if (!socket || socket.readyState !== WebSocket.OPEN) return;

    socket.send(JSON.stringify({
        roomId: Number(roomId),
        sender: loginId,
        message: msg
    }));

    $("#message").val("");
}

$("#message").on("keydown", function (e) {
    if (e.keyCode === 13) sendMessage();
});

window.addEventListener("beforeunload", function () {
    if (socket) socket.close();
});

connect();
</script>

<style>
body{
    background:#f5f7fb;
}

/* 전체 */
.chat-wrapper{
    width:900px;
    height:800px;
    margin:30px auto;
    background:#fff;
    border-radius:20px;
    overflow:hidden;
    display:flex;
    flex-direction:column;
    box-shadow:0 10px 35px rgba(0,0,0,.12);
}


/* 헤더 */
.chat-header{
    height:75px;
    background:#1f2937;
    color:#fff;
    display:flex;
    align-items:center;
    justify-content:space-between;
    padding:0 30px;
    font-size:22px;
    font-weight:bold;
}

.chat-header::after{
    content:"상담중";
    font-size:14px;
    background:#22c55e;
    padding:5px 12px;
    border-radius:20px;
}


/* 채팅영역 */
#chatBox{
    flex:1;
    overflow-y:auto;
    padding:30px;
    background:#f3f4f6;
}


/* 메시지 */
.message{
    display:flex;
    flex-direction:column;
    margin-bottom:20px;
}

.other{
    align-items:flex-start;
}

.mine{
    align-items:flex-end;
}

.sender{
    font-size:12px;
    color:#777;
    margin-bottom:5px;
}


/* 말풍선 */
.bubble{
    max-width:65%;
    padding:15px 18px;
    border-radius:20px;
    font-size:15px;
    line-height:1.5;
    word-break:break-word;
    box-shadow:0 3px 10px rgba(0,0,0,.08);
}


/* 사용자 */
.other .bubble{
    background:white;
    border-top-left-radius:5px;
}


/* 관리자 */
.mine .bubble{
    background:#2563eb;
    color:white;
    border-top-right-radius:5px;
}


/* 입력영역 */
.chat-input{
    height:90px;
    background:white;
    border-top:1px solid #e5e7eb;
    padding:15px 20px;
    display:flex;
    align-items:center;
    gap:15px;
}


/* 입력창 */
.chat-input input{
    flex:1;
    height:50px;
    border:1px solid #d1d5db;
    border-radius:30px;
    padding:0 20px;
    font-size:15px;
    outline:none;
}

.chat-input input:focus{
    border-color:#2563eb;
}


/* 버튼 */
.chat-input button{
    width:110px;
    height:50px;
    border:none;
    border-radius:30px;
    background:#2563eb;
    color:white;
    font-size:15px;
    font-weight:bold;
    cursor:pointer;
    transition:.2s;
}

.chat-input button:hover{
    background:#1d4ed8;
}
</style>