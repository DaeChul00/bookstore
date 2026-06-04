<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container" style="width:800px; margin:50px auto;">

    <h2>문의 상세보기</h2>
    <hr>

    <table border="1" width="100%" cellpadding="10" cellspacing="0">
        <tr>
            <th>번호</th>
            <td>${cv.id}</td>
        </tr>
        <tr>
            <th>작성자</th>
            <td>${cv.userName}</td>
        </tr>
        <tr>
            <th>제목</th>
            <td>${cv.title}</td>
        </tr>
        <tr>
            <th>내용</th>
            <td style="height:150px;">${cv.content}</td>
        </tr>
        <tr>
            <th>카테고리</th>
            <td>${cv.category}</td>
        </tr>
        <tr>
            <th>상태</th>
            <td>${cv.status}</td>
        </tr>
        <tr>
            <th>답변</th>
            <td style="height:150px;">
                <c:choose>
                    <c:when test="${not empty cv.answer}">
                        ${cv.answer}
                    </c:when>
                    <c:otherwise>
                        <span style="color:gray;">아직 답변이 없습니다.</span>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>관리자</th>
            <td>${cv.adminId}</td>
        </tr>
        <tr>
            <th>작성일</th>
            <td>${cv.createdAt}</td>
        </tr>
        <tr>
            <th>답변일</th>
            <td>${cv.answeredAt}</td>
        </tr>
    </table>

    <br>

    <div style="text-align:center;">
        <a href="/cs/csList">목록</a>
    </div>

</div>