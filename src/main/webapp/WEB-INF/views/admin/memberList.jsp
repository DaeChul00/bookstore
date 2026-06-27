<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<style>
    .admin-container { width: 1000px; margin: 40px auto; font-family: 'Malgun Gothic'; }
    .admin-table { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; }
    .admin-table th { background: #2c3e50; color: white; padding: 12px; }
    .admin-table td { border: 1px solid #ddd; padding: 10px; text-align: center; }
    .btn-submit { padding: 5px 10px; cursor: pointer; background: #2980b9; color: white; border: none; border-radius: 3px; }
    .btn-delete { background-color: #d9534f; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer; margin-left: 5px; }
</style>

<div class="admin-container">
    <h2>👤 전체 회원 관리</h2>
    <hr>
    
    <select style="padding: 5px; border-radius: 4px;" 
            onchange="location.href='${pageContext.request.contextPath}/admin/memberList?sort=' + this.value">
        <option value="new" ${currentSort == 'new' ? 'selected' : ''}>최신 가입순</option>
        <option value="old" ${currentSort == 'old' ? 'selected' : ''}>오래된 가입순</option>
        <option value="name" ${currentSort == 'name' ? 'selected' : ''}>이름순</option>
        <option value="id" ${currentSort == 'id' ? 'selected' : ''}>아이디순</option>
    </select>
    
    <table class="admin-table">
        <thead>
            <tr>
                <th>아이디</th>
                <th>이름</th>
                <th>이메일</th>
                <th>권한 설정</th>
                <th>가입일</th>
                <th>관리</th>
            </tr>
        </thead>
        <tbody>
		    <c:forEach var="user" items="${userList}">
		        <c:if test="${user.role != 'ROLE_ADMIN' && user.role != 'ADMIN'}">
		            <tr>
		                <td>${user.memberId}</td> 
		                <td>${user.name}</td>
		                <td>${user.email}</td>
		                <td>
		                    <form action="${pageContext.request.contextPath}/admin/changeRole" method="post" style="display: flex; justify-content: center; gap: 5px; margin: 0;">
		                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		                        <input type="hidden" name="memberId" value="${user.memberId}">
		                        
		                        <select name="role" style="padding: 5px; border-radius: 4px;">
		                            <option value="USER" ${user.role.contains('USER') && !user.role.contains('ADMIN') ? 'selected' : ''}>USER</option>
		                            <option value="USER_ADMIN" ${user.role.contains('USER_ADMIN') ? 'selected' : ''}>USER_ADMIN</option>
		                            <option value="BOOK_ADMIN" ${user.role.contains('BOOK_ADMIN') ? 'selected' : ''}>BOOK_ADMIN</option>
		                        </select>
		                        <button type="submit" class="btn-submit">변경</button>
		                    </form>
		                </td>
		                <td>${user.regdate}</td>
		                <td>
		                    <button type="button" class="btn-delete" 
		                            onclick="if(confirm('${user.memberId} 회원을 강제 탈퇴시키겠습니까?')) 
		                            location.href='${pageContext.request.contextPath}/admin/deleteMember?memberId=${user.memberId}'">
		                        삭제
		                    </button>
		                </td>
		            </tr>
		        </c:if>
		    </c:forEach>
		</tbody>
    </table>
</div>