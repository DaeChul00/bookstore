<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize access="isAnonymous()">
    <script>
        alert("고객센터는 회원 전용 서비스입니다. 로그인 창으로 이동합니다.");
        window.location.href = "${pageContext.request.contextPath}/login";
    </script>
</sec:authorize>

<div class="container bg-white p-5 rounded shadow-sm">
    <h2 class="text-center mb-4">문의사항 게시판</h2>

    <div class="d-flex justify-content-between mb-3">
        <form class="d-flex">
            <input type="text" class="form-control me-2" placeholder="검색어 입력">
            <button class="btn btn-outline-primary" type="button">검색</button>
        </form>
        <button class="btn btn-primary" onclick="location.href='insertform'">글쓰기</button>
    </div>

    <table class="table table-hover border-top">
        <thead class="table-light text-center">
            <tr>
                <th style="width: 80px;">번호</th>
                <th>제목</th>
                <th style="width: 120px;">작성자</th>
                <th style="width: 150px;">작성일</th>
                <th style="width: 100px;">상태</th>
            </tr>
        </thead>
        <tbody class="text-center">
            <c:if test="${empty csList}">
                <tr><td colspan="5">등록된 문의사항이 없습니다.</td></tr>
            </c:if>
            <c:forEach var="item" items="${csList}">
                <tr>
                    <td>${item.id}</td>
                    
                    <td class="text-center" style="cursor: pointer;" onclick="location.href='view?id=${item.id}'">
                        <span class="badge bg-info me-2">${item.category}</span> ${item.title}
                    </td>
                    
                    <td>${item.userName}</td>
                    
                    <td><fmt:formatDate value="${item.createdAt}" pattern="yyyy-MM-dd" /></td>
         
                    <td>
                        <c:choose>
                            <c:when test="${item.status eq '답변대기' || item.status eq 'WAIT'}">
                                <span class="badge bg-warning text-dark">
                                    답변대기
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-success">
                                    답변완료
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>