<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<h2 style="text-align: center; margin: 30px 0;"> 도서 관리 및 판매 통계
		대시보드</h2>

	<div class="dashboard-container">
		<div class="stat-card">
			<h3> 최근 7일 매출 추이</h3>
			<div class="chart-container">
				<canvas id="salesLineChart"></canvas>
			</div>
		</div>

		<div class="stat-card">
			<h3> 출판사별 도서 비중</h3>
			<div class="donut-chart-wrapper">
				<div class="chart-container" style="width: 50%; height: 350px;">
					<canvas id="publisherDonutChart"></canvas>
				</div>
			</div>
		</div>

		<div class="stat-card" style="flex: 2;">
			<h3> 실시간 베스트셀러 TOP 5</h3>
			<div class="chart-container">
				<canvas id="bestSellerBarChart"></canvas>
			</div>
		</div>

		<div class="stat-card" style="flex: 1;">
			<h3>⭐ 인기 도서 (평점순)</h3>
			<table>
				<thead>
					<tr>
						<th>도서명</th>
						<th>평점</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="book" items="${topBooks}">
						<tr>
							<td>${book.title}</td>
							<td><b style="color: #f39c12;">${book.rating}</b></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<div class = "btn-wrap">
			<button class="btn-main" 
			onclick="location.href='${pageContext.request.contextPath}/book/list' ">메인화면으로</button>
		</div>
	</div>

	<script>
        // 공통 색상 팔레트
        const colors = ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b'];

        // [데이터 1] 매출 추이 데이터 가공
        const salesLabels = [];
        const salesValues = [];
        <c:forEach var="sale" items="${dailySales}">
            salesLabels.push('${sale.DATE}');
            salesValues.push(${sale.TOTAL_SALES});
        </c:forEach>

        // [차트 1] 매출 꺾은선 그래프
        new Chart(document.getElementById('salesLineChart'), {
            type: 'line',
            data: {
                labels: salesLabels.reverse(), // 날짜순 정렬
                datasets: [{
                    label: '일별 매출액',
                    data: salesValues.reverse(),
                    borderColor: '#4e73df',
                    backgroundColor: 'rgba(78, 115, 223, 0.1)',
                    fill: true,
                    tension: 0.3
                }]
            }
        });

        // [데이터 2] 출판사 데이터 가공
        const pubLabels = [];
        const pubData = [];
        <c:forEach var="stat" items="${publisherStats}">
            pubLabels.push('${stat.PUBLISHER}');
            pubData.push(${stat.COUNT});
        </c:forEach>

        // [차트 2] 출판사 도넛 차트
        new Chart(document.getElementById('publisherDonutChart'), {
            type: 'doughnut',
            data: {
                labels: pubLabels,
                datasets: [{
                    data: pubData,
                    backgroundColor: colors
                }]
            },
            options: { cutout: '70%' }
        });

        // [데이터 3] 베스트셀러 데이터 가공
        const bestLabels = [];
        const bestData = [];
        <c:forEach var="best" items="${bestSellers}">
            bestLabels.push('${best.TITLE}');
            bestData.push(${best.TOTAL_COUNT});
        </c:forEach>

        // [차트 3] 베스트셀러 막대 그래프
        new Chart(document.getElementById('bestSellerBarChart'), {
            type: 'bar',
            data: {
                labels: bestLabels,
                datasets: [{
                    label: '판매량(권)',
                    data: bestData,
                    backgroundColor: '#1cc88a'
                }]
            },
            options: { indexAxis: 'y' } // 가로 막대 그래프
        });
    </script>
</body>
</html>