<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container my-5">
    <div class="card shadow-sm border-0 p-4">
        <h3>✏️ 문의사항 수정하기</h3>
        <form action="${pageContext.request.contextPath}/cs/update" method="post" class="mt-4">
            <input type="hidden" name="id" value="${cv.id}">
            <div class="mb-3">
                <label class="form-label fw-bold">제목</label>
                <input type="text" class="form-control" name="title" value="${cv.title}" required>
            </div>
            <div class="mb-3">
                <label class="form-label fw-bold">카테고리</label>
                <input type="text" class="form-control" name="category" value="${cv.category}" readonly>
            </div>
            <div class="mb-3">
                <label class="form-label fw-bold">내용</label>
                <textarea class="form-control" name="content" rows="6" required>${cv.content}</textarea>
            </div>
            <div class="text-center mt-4">
                <button type="submit" class="btn btn-warning me-2">수정 완료</button>
                <a href="${pageContext.request.contextPath}/cs/view?id=${cv.id}" class="btn btn-secondary">취소</a>
            </div>
        </form>
    </div>
</div>