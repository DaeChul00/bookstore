<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:forEach var="room" items="${roomList}">
        <div class="room-card">

            <!-- LEFT -->
            <div class="room-info">
                <div class="room-users">
                    👤 ${room.username1} ↔ ${room.username2}
                </div>

                <div class="room-message">
                    <c:choose>
                        <c:when test="${empty room.lastMessage}">
                            아직 메시지가 없습니다.
                        </c:when>
                        <c:otherwise>
                            ${room.lastMessage}
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="room-meta">
                    ROOM #${room.roomId}
                </div>
            </div>

            <!-- RIGHT -->
            <div class="room-right">
                <div class="room-meta">
                    <fmt:formatDate value="${room.lastTime}" pattern="yyyy-MM-dd HH:mm"/>
                </div>

                <br>

                <a class="enter-btn"
                   href="${pageContext.request.contextPath}/chat/room?roomId=${room.roomId}">
                    입장
                </a>
            </div>

        </div>
    </c:forEach>