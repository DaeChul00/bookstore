<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="/css/style.css">
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