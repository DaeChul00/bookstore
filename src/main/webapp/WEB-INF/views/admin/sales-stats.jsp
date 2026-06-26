<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
    .dashboard-container { max-width: 1200px; margin: 40px auto; display: flex; flex-wrap: wrap; gap: 20px; }
    .stat-card { background: #fff; border-radius: 15px; padding: 25px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); box-sizing: border-box; }
    
    /* 섹션 그룹화 */
    .sales-group { flex: 1 1 100%; display: flex; gap: 20px; flex-wrap: wrap; }
    
    /* 큰 차트 (7일 매출 & 출판사 비중) 전용 스타일 */
    .stat-card.full-width { 
        flex: 1 1 100%; 
        max-width: 100%;
    }
    /* 차트가 가로로 너무 늘어져서 깨지는 것 방지 및 중앙 정렬 */
    .large-chart-wrapper {
        max-width: 850px; 
        margin: 0 auto;
    }

    /* 하단 작은 카드들 */
    .detail-group { flex: 1 1 100%; display: flex; gap: 20px; flex-wrap: wrap; }
    .detail-group .stat-card { flex: 1 1 350px; }
    
    .chart-container { position: relative; height: 250px; margin-top: 15px; }
    h3 { margin: 0; color: #2c3e50; font-size: 16px; font-weight: 700; }
    
    table { width: 100%; border-collapse: collapse; margin-top: 15px; }
    table th { background: #f8f9fa; padding: 10px; text-align: left; }
    table td { padding: 10px; border-bottom: 1px solid #eee; }
    
    /* 평점 도서 링크 */
    .book-link { text-decoration: none; color: inherit; }
    .book-link:hover { color: #3498db; text-decoration: underline; }

    .btn-wrap { width: 100%; text-align: center; margin-top: 30px; }
    .btn-main { background: #2c3e50; color: #fff; border: none; padding: 12px 30px; border-radius: 30px; cursor: pointer; font-weight: bold; }
</style>

<h2 style="text-align:center; margin:40px 0; color:#2c3e50; font-weight:800;">📊 판매 통계 대시보드</h2>

<div class="dashboard-container">
    <div class="sales-group">
        <div class="stat-card full-width">
            <h3>📈 최근 7일 매출 추이</h3>
            <div class="large-chart-wrapper">
                <div class="chart-container" style="height:300px;"><canvas id="salesLineChart"></canvas></div>
            </div>
        </div>

        <div class="stat-card full-width">
            <h3>📚 출판사별 매출 비중</h3>
            <div class="large-chart-wrapper">
                <div class="chart-container" style="height:350px;"><canvas id="publisherSalesChart"></canvas></div>
            </div>
        </div>

        <div class="stat-card"><h3>📅 주간 매출</h3><div class="chart-container"><canvas id="weeklySalesChart"></canvas></div></div>
        <div class="stat-card"><h3>🗓 월간 매출</h3><div class="chart-container"><canvas id="monthlySalesChart"></canvas></div></div>
        <div class="stat-card"><h3>📆 연간 매출</h3><div class="chart-container"><canvas id="yearlySalesChart"></canvas></div></div>
    </div>

    <div class="detail-group">
        <div class="stat-card"><h3>🔥 베스트셀러 TOP 5</h3><div class="chart-container"><canvas id="bestSellerBarChart"></canvas></div></div>
        <div class="stat-card">
            <h3>⭐ 평점 TOP 도서</h3>
            <div class="chart-container" style="overflow-y:auto; height:250px;">
                <table>
                    <thead><tr><th>도서명</th><th>평점</th></tr></thead>
                    <tbody>
                        <c:forEach var="book" items="${topBooks}">
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/book/view?id=${book.id}" class="book-link">
                                        <b style="color:#2c3e50;"><c:out value="${book.title}"/></b>
                                    </a>
                                </td>
                                <td><b style="color:#e67e22;"><fmt:formatNumber value="${book.rating}" pattern="0.0"/></b></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="btn-wrap">
        <button class="btn-main" onclick="location.href='${pageContext.request.contextPath}/book/list'">메인으로 돌아가기</button>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>
const colors = ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b', '#858796', '#f39c12'];

// 따옴표(') 처리 함수
function escapeJS(str) {
    if(!str) return "";
    return str.replace(/'/g, "\\'");
}

/* 1. 최근 7일 매출 */
const salesLabels = [];
const salesValues = [];
<c:forEach var="sale" items="${dailySales}">
    (function() {
        let d = '${sale.DATE}';
        if(d.length > 10) d = d.substring(0, 10); // 날짜 HH:mm... 자르기
        salesLabels.push(d);
        salesValues.push(${sale.TOTAL_SALES});
    })();
</c:forEach>

new Chart(document.getElementById('salesLineChart'), {
    type: 'line',
    data: {
        labels: [...salesLabels].reverse(),
        datasets: [{
            label: '매출액',
            data: [...salesValues].reverse(),
            borderColor: '#4e73df',
            backgroundColor: 'rgba(78,115,223,0.1)',
            fill: true,
            tension: 0.3
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            x: { 
                ticks: { autoSkip: false } // 날짜 모두 보이기
            }
        }
    }
});

/* 2. 출판사별 매출 비중 (크게) */
const publisherSalesLabels = [];
const publisherSalesData = [];
<c:forEach var="sale" items="${publisherSales}">
    publisherSalesLabels.push(escapeJS('<c:out value="${sale.PUBLISHER}" escapeXml="false" />'));
    publisherSalesData.push(${sale.TOTAL_SALES});
</c:forEach>

new Chart(document.getElementById('publisherSalesChart'), {
    type: 'doughnut',
    data: {
        labels: publisherSalesLabels,
        datasets: [{
            data: publisherSalesData,
            backgroundColor: colors
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                position: 'right' // 차트가 커졌으므로 범례를 오른쪽에 배치
            }
        }
    }
});

/* 3. 베스트셀러 */
const bestLabels = [];
const bestData = [];
<c:forEach var="best" items="${bestSellers}">
    bestLabels.push(escapeJS('<c:out value="${best.TITLE}" escapeXml="false" />'));
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
    options: { indexAxis: 'y', responsive: true, maintainAspectRatio: false }
});

/* 기타 작은 차트들 (주간, 월간, 연간) */
function createSmallChart(id, labels, data, label, color, type) {
    new Chart(document.getElementById(id), {
        type: type,
        data: {
            labels: labels.reverse(),
            datasets: [{
                label: label,
                data: data.reverse(),
                backgroundColor: color,
                borderColor: color,
                fill: type === 'line'
            }]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });
}

// 데이터 바인딩
const wL = [], wD = [], mL = [], mD = [], yL = [], yD = [];
<c:forEach var="s" items="${weeklySales}"> wL.push('${s.SALE_WEEK}'); wD.push(${s.TOTAL_SALES}); </c:forEach>
<c:forEach var="s" items="${monthlySales}"> mL.push('${s.SALE_MONTH}'); mD.push(${s.TOTAL_SALES}); </c:forEach>
<c:forEach var="s" items="${yearlySales}"> yL.push('${s.SALE_YEAR}'); yD.push(${s.TOTAL_SALES}); </c:forEach>

createSmallChart('weeklySalesChart', wL, wD, '주간 매출', '#36b9cc', 'bar');
createSmallChart('monthlySalesChart', mL, mD, '월간 매출', '#f6c23e', 'line');
createSmallChart('yearlySalesChart', yL, yD, '연간 매출', '#e74a3b', 'bar');

</script>