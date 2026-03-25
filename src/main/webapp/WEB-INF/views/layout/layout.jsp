<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

<style>

body{
    margin:0;
    height:100vh;
    overflow:hidden;   /* body 스크롤 제거 */
}

/* 전체 레이아웃 */
.wrapper{
    height:100%;
    display:flex;
    flex-direction:column;
}

/* header */
header{
    height:80px;
    background:#343a40;
    color:white;
}

/* footer */
footer{
    height:60px;
    background:#343a40;
    color:white;
}

/* main */
main{
    flex:1;
    overflow-y:auto;   /* 내부 스크롤 */
    padding:20px;
}

</style>
</head>

<body>

<div class="wrapper">

<!-- HEADER -->
    <header class="d-flex align-items-center justify-content-center">
        <jsp:include page="/WEB-INF/views/common/header.jsp"/>
    </header>

    <!-- MAIN -->
    <main>
        <jsp:include page="${contentPage}"/>
    </main>

    <!-- FOOTER -->
    <footer class="d-flex align-items-center justify-content-center">
        <jsp:include page="/WEB-INF/views/common/footer.jsp"/>
    </footer>

</div>

</body>
</html>