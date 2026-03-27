<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<style>
.insert-container {
    width: 700px;
    margin: 50px auto;
}

/* 카드 */
.insert-card {
    background: #fff;
    padding: 30px;
    border-radius: 12px;
    box-shadow: 0 5px 20px rgba(0,0,0,0.05);
}

/* 제목 */
.insert-title {
    font-size: 22px;
    font-weight: bold;
    margin-bottom: 20px;
    color: #2c3e50;
}

/* 그룹 */
.form-group {
    margin-bottom: 18px;
}

.form-group label {
    display: block;
    font-weight: bold;
    margin-bottom: 6px;
    font-size: 14px;
}

/* 입력 */
.form-group input,
.form-group textarea {
    width: 100%;
    padding: 10px 12px;
    border-radius: 6px;
    border: 1px solid #ddd;
    font-size: 14px;
    transition: 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
    border-color: #2c3e50;
    outline: none;
}

/* textarea */
textarea {
    resize: none;
    height: 120px;
}

/* 버튼 영역 */
.btn-area {
    text-align: right;
    margin-top: 25px;
}

/* 버튼 */
.btn {
    padding: 10px 18px;
    border-radius: 6px;
    border: none;
    font-weight: bold;
    cursor: pointer;
    text-decoration: none;
    font-size: 14px;
}

/* 등록 버튼 */
.btn-submit {
    background: #2c3e50;
    color: #fff;
}

.btn-submit:hover {
    background: #1a252f;
}

/* 취소 */
.btn-cancel {
    background: #aaa;
    color: #fff;
    margin-left: 10px;
}

.btn-cancel:hover {
    background: #888;
}
</style>

<div class="insert-container">
    <div class="insert-card">

        <div class="insert-title">📚 도서 등록</div>

        <form action="${pageContext.request.contextPath}/book/insert" method="post">

            <div class="form-group">
                <label>ISBN</label>
                <input type="text" name="isbn">
            </div>

            <div class="form-group">
                <label>도서 이미지 URL</label>
                <input type="url" name="bookimage" placeholder="https://example.com/book.jpg">
            </div>

            <div class="form-group">
                <label>제목</label>
                <input type="text" name="title" required>
            </div>

            <div class="form-group">
                <label>저자</label>
                <input type="text" name="author" required>
            </div>

            <div class="form-group">
                <label>출판사</label>
                <input type="text" name="publisher">
            </div>

            <div class="form-group">
                <label>출판일</label>
                <input type="date" name="publictiondate">
            </div>

            <div class="form-group">
                <label>가격</label>
                <input type="number" name="price" required>
            </div>

            <div class="form-group">
                <label>평점</label>
                <input type="number" step="0.1" name="rating" min="0" max="10">
            </div>

            <div class="form-group">
                <label>내용</label>
                <textarea name="content"></textarea>
            </div>

            <div class="btn-area">
                <button type="submit" class="btn btn-submit">등록</button>
                <a href="${pageContext.request.contextPath}/book/list" class="btn btn-cancel">취소</a>
            </div>

        </form>
    </div>
</div>
</html>