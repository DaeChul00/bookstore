<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    .admin-container { width: 1000px; margin: 40px auto; font-family: 'Malgun Gothic'; }
    .admin-table { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; }
    .admin-table th { background: #2c3e50; color: white; padding: 12px; }
    .admin-table td { border: 1px solid #ddd; padding: 10px; text-align: center; }
    .role-admin { color: #d9534f; font-weight: bold; }
    .role-user { color: #333; }
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
        <thead style="text-align: center;">
            <tr>
                <th>아이디</th>
                <th>이름</th>
                <th>이메일</th>
                <th>권한</th>
                <th>가입일</th>
                <th>관리</th>
            </tr>
        </thead>
        <tbody style="text-align: center;">
		    <c:forEach var="user" items="${userList}">
		        <tr>
		            <%-- user.id를 user.memberId로 변경 --%>
		            <td>${user.memberId}</td> 
		            <td>${user.name}</td>
		            <td>${user.email}</td>
		            <td>
					    <span class="role-badge ${user.role == 'ADMIN' ? 'admin' : 'user'}">
					        ${user.role}
					    </span>
					</td>
					<td>${user.regdate}</td>
					<td>
					    <button type="button" class="btn-role"
					            onclick="if(confirm('${user.role} 권한을 변경하시겠습니까?')) 
					            location.href='${pageContext.request.contextPath}/admin/changeRole?memberId=${user.memberId}&role=${user.role}'">
					        권한변경
					    </button>
					
					    <button type="button" class="btn-delete" 
					            style="background-color: #d9534f; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer; margin-left: 5px;"
					            onclick="if(confirm('${user.memberId} 회원을 강제 탈퇴시키겠습니까?')) 
					            location.href='${pageContext.request.contextPath}/admin/deleteMember?memberId=${user.memberId}'">
					        삭제
					    </button>
					</td>
		        </tr>
		    </c:forEach>
		</tbody>
    </table>
</div>

