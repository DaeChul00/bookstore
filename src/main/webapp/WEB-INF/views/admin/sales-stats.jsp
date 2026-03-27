<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
/* 전체 레이아웃 */
.dashboard-container {
    max-width: 1200px;
    margin: 40px auto;
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}

/* 카드 */
.stat-card {
    background: #fff;
    border-radius: 15px;
    padding: 20px;
    flex: 1 1 45%;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    transition: all 0.2s ease;
}

.stat-card:hover {
    transform: translateY(-5px);
}

/* 제목 */
.stat-card h3 {
    margin-bottom: 15px;
    color: #2c3e50;
    font-size: 16px;
    font-weight: 700;
}

/* 차트 */
.chart-container {
    position: relative;
    height: 300px;
}

/* 테이블 */
table {
    width: 100%;
    border-collapse: collapse;
    font-size: 14px;
}

table th {
    background: #f8f9fa;
    padding: 10px;
}

table td {
    padding: 10px;
    border-bottom: 1px solid #eee;
}

/* 버튼 */
.btn-wrap {
    width: 100%;
    text-align: center;
    margin-top: 30px;
}

.btn-main {
    background: #2c3e50;
    color: #fff;
    border: none;
    padding: 12px 30px;
    border-radius: 30px;
    cursor: pointer;
    font-weight: bold;
}

.btn-main:hover {
    background: #1a252f;
}
</style>

<h2 style="text-align:center; margin:40px 0; color:#2c3e50; font-weight:800;">
    📊 판매 통계 대시보드
</h2>

<div class="dashboard-container">

    <!-- 매출 그래프 (크게) -->
    <div class="stat-card" style="flex:1 1 100%;">
        <h3>📈 최근 7일 매출 추이</h3>
        <div class="chart-container">
            <canvas id="salesLineChart"></canvas>
        </div>
    </div>

    <!-- 출판사 -->
    <div class="stat-card">
        <h3>📚 출판사별 도서 비중</h3>
        <div class="chart-container">
            <canvas id="publisherDonutChart"></canvas>
        </div>
    </div>

    <!-- 베스트셀러 -->
    <div class="stat-card">
        <h3>🔥 베스트셀러 TOP 5</h3>
        <div class="chart-container">
            <canvas id="bestSellerBarChart"></canvas>
        </div>
    </div>

    <!-- 평점 -->
    <div class="stat-card">
        <h3>⭐ 평점 TOP 도서</h3>
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
                        <td><b style="color:#2c3e50;">${book.title}</b></td>
                        <td>
                            <b style="color:#e67e22;">
                                <fmt:formatNumber value="${book.rating}" pattern="0.0"/>
                            </b>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- 버튼 -->
    <div class="btn-wrap">
        <button class="btn-main"
            onclick="location.href='${pageContext.request.contextPath}/book/list'">
            메인으로 돌아가기
        </button>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>
const colors = ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b'];

/* 매출 */
const salesLabels = [];
const salesValues = [];
<c:forEach var="sale" items="${dailySales}">
    salesLabels.push('${sale.DATE}');
    salesValues.push(${sale.TOTAL_SALES});
</c:forEach>

new Chart(document.getElementById('salesLineChart'), {
    type: 'line',
    data: {
        labels: salesLabels.reverse(),
        datasets: [{
            label: '매출액',
            data: salesValues.reverse(),
            borderColor: '#4e73df',
            backgroundColor: 'rgba(78,115,223,0.1)',
            fill: true,
            tension: 0.3
        }]
    }
});

/* 출판사 */
const pubLabels = [];
const pubData = [];
<c:forEach var="stat" items="${publisherStats}">
    pubLabels.push('${stat.PUBLISHER}');
    pubData.push(${stat.COUNT});
</c:forEach>

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

/* 베스트셀러 */
const bestLabels = [];
const bestData = [];
<c:forEach var="best" items="${bestSellers}">
    bestLabels.push('${best.TITLE}');
    bestData.push(${best.TOTAL_COUNT});
</c:forEach>

new Chart(document.getElementById('bestSellerBarChart'), {
    type: 'bar',
    data: {
        labels: bestLabels,
        datasets: [{
            label: '판매량',
            data: bestData,
            backgroundColor: '#1cc88a'
        }]
    },
    options: {
        indexAxis: 'y'
    }
});
</script>