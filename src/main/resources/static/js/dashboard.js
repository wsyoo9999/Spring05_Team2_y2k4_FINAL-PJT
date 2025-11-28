export async function show_charts(formData) {
    const html = `
  <div class="table-scroll-wrapper">

       <div style="padding: 20px;">

      <!-- 상단(KPI / 선그래프) -->
      <div style="
        display: flex;
        gap: 20px;
        align-items: stretch;
        margin-bottom: 20px;
        flex-wrap: wrap;
      ">

        <!-- KPI 영역 -->
        <div style=" flex: 1.5 1 260px; display: flex; ">
          <div style="
            width: 100%;
            border-radius: 24px;
            border: 1px solid #4A90E2;
            padding: 32px 36px;
            box-sizing: border-box;
            text-align: center;
            background-color: #4A90E2;
            display: flex;
            flex-direction: column;
            justify-content: center;
          ">
            <div style="font-size: 15px; color: #FFFFFF; margin-bottom: 18px;">
              금월 총 판매금액
            </div>
        
            <div id="kpi-this-month-amount"
                 style="font-size: 34px; font-weight: 700; color: #FFFFFF; margin-bottom: 6px;">
              0원
            </div>
        
            <div id="kpi-change-rate"
                 style="font-size: 18px; font-weight: 700; margin-bottom: 20px; color: #FFFFFF;">
              0.0%
            </div>
        
            <div style="font-size: 14px; color: #FFFFFF; margin-bottom: 6px;">
              전월 총 판매금액
            </div>
        
            <div id="kpi-last-month-amount"
                 style="font-size: 20px; font-weight: 500; color: #FFFFFF;">
              0원
            </div>
          </div>
        </div>

        <!-- 선 그래프 (폭 900px) -->
       <div style="
          flex: 8.5 1 400px;       
          display: flex;
          justify-content: center;
        ">
          <div style="
            border-radius: 24px;
            border: 1px solid #EEEEEE;
            background-color: #fff;
            padding: 20px 24px;
            box-sizing: border-box;
            display: flex;
            flex-direction: column;
            width: 100%;           
            max-width: 100%;     
          ">
            <div style="font-size: 15px; font-weight: 600; color: #333; margin-bottom: 10px;">
              월별 총 판매금액 추이
            </div>
            <div style="position: relative; width: 100%; height: 340px;">
              <canvas id="showSaleLine"></canvas>
            </div>
          </div>
        </div>

      </div>

      <!-- 하단( 판매 / 구매 ) -->
      <div style="
        display: flex;
        justify-content: center;
        gap: 20px;
        flex-wrap: wrap;
      ">
        <!-- 판매 도넛 -->
        <div style="
          flex: 1 1 0;              
          min-width: 320px;
          max-width: 600px;
          border-radius: 24px;
          border: 1px solid #EEEEEE;
          background-color: #fff;
          padding: 20px 24px;
          box-sizing: border-box;
          display: flex;
          flex-direction: column;
        ">
          <h3 style="text-align:center; margin-bottom: 10px; font-size: 15px;">
            최근 1년 <strong>판매금액</strong> TOP5
          </h3>
          <div style=" flex: 1; display: flex; justify-content: center; align-items: center; width: 100%; height: 260px; ">
            <canvas id="showSaleDonut" style="max-width: 260px; max-height: 260px;"></canvas>
            </div>
        </div>

        <!-- 구매 도넛 -->
        <div style="
          flex: 1 1 0;      
          min-width: 320px;
          max-width: 600px;
          border-radius: 24px;
          border: 1px solid #EEEEEE;
          background-color: #fff;
          padding: 20px 24px;
          box-sizing: border-box;
          display: flex;
          flex-direction: column;
        ">
          <h3 style="text-align:center; margin-bottom: 10px; font-size: 15px;">
            최근 1년 <strong>구매금액</strong> TOP5
          </h3>
          <div style=" flex: 1; display: flex; justify-content: center; align-items: center; width: 100%; height: 260px; ">
            <canvas id="showPurchaseDonut" style="max-width: 260px; max-height: 260px;"></canvas>
            </div>
        </div>
      </div>

    </div>
    </div>
  `;

    // 아래에서 KPI/차트 그리기
    setTimeout(() => {
        const showSaleDonut     = document.getElementById('showSaleDonut');
        const showPurchaseDonut = document.getElementById('showPurchaseDonut');
        const showSaleLine      = document.getElementById('showSaleLine');
        const kpiThisMonth      = document.getElementById('kpi-this-month-amount');
        const kpiLastMonth      = document.getElementById('kpi-last-month-amount');
        const kpiChangeRate     = document.getElementById('kpi-change-rate');

        if (typeof Chart === 'undefined') {
            console.error('Chart.js가 로드되지 않았습니다.');
            return;
        }

        // ✅ 1) KPI
        $.get('/api/dashboard/showKpi/saleThisAndLast')
            .done((res) => {
                const thisMonth = Number(res.thisMonthSale || 0);
                const lastMonth = Number(res.lastMonthSale || 0);

                if (kpiThisMonth) {
                    kpiThisMonth.textContent =
                        thisMonth.toLocaleString() + '원';
                }
                if (kpiLastMonth) {
                    kpiLastMonth.textContent =
                        lastMonth.toLocaleString() + '원';
                }
                if (kpiChangeRate) {
                    let text = '전월 대비 변화 없음';
                    let color = '#333';

                    if (lastMonth === 0 && thisMonth === 0) {
                        text = '전월 대비 변화 없음';
                    } else if (lastMonth === 0 && thisMonth !== 0) {
                        text = '전월 대비 +∞%';
                    } else {
                        const diff = thisMonth - lastMonth;
                        const rate = (diff / lastMonth) * 100;
                        if (diff > 0) {
                            text  = `▲ ${Math.abs(rate).toFixed(1)}%`;
                            color = '#d32f2f';
                        } else if (diff < 0) {
                            text  = `▼ ${Math.abs(rate).toFixed(1)}%`;
                            color = '#1976d2';
                        } else {
                            text = '변화 없음 (0.0%)';
                        }
                    }
                    kpiChangeRate.textContent = text;
                    kpiChangeRate.style.color = color;
                }
            })
            .fail((err) => {
                console.error('/api/dashboard/showKpi/saleThisAndLast 호출 중 에러', err);
                if (kpiThisMonth)  kpiThisMonth.textContent  = '-';
                if (kpiLastMonth)  kpiLastMonth.textContent  = '-';
                if (kpiChangeRate) kpiChangeRate.textContent = '데이터 오류';
            });

        // ✅ 2) 판매 도넛
        if (showSaleDonut) {
            $.get('/api/dashboard/showChart/saleDonut')
                .done((res) => {
                    const labels = res.map(r => r.stock_name);
                    const data   = res.map(r => r.total_price_sum);

                    const existing1 = Chart.getChart(showSaleDonut);
                    if (existing1) existing1.destroy();

                    new Chart(showSaleDonut, {
                        type: 'doughnut',
                        data: {
                            labels,
                            datasets: [{
                                label: '단위: 원',
                                data,
                                backgroundColor: [
                                    'rgba(255, 99, 132, 0.7)',
                                    'rgba(54, 162, 235, 0.7)',
                                    'rgba(255, 206, 86, 0.7)',
                                    'rgba(75, 192, 192, 0.7)',
                                    'rgba(153, 102, 255, 0.7)'
                                ],
                                borderWidth: 1
                            }]
                        },
                        options: {
                            responsive: true,
                            cutout: '60%',
                            plugins: {
                                legend: { position: 'bottom' },
                                title: {
                                    display: true,
                                    text: '최근 1년 판매금액 TOP5 (단위: 원)'
                                }
                            }
                        }
                    });
                })
                .fail((err) => {
                    console.error('/api/dashboard/showChart/saleDonut 호출 중 에러 발생', err);
                });
        }

        // ✅ 3) 구매 도넛
        if (showPurchaseDonut) {
            $.get('/api/dashboard/showChart/purchaseDonut')
                .done((res) => {
                    const labels = res.map(r => r.stock_name);
                    const data   = res.map(r => r.total_price_sum);

                    const existing3 = Chart.getChart(showPurchaseDonut);
                    if (existing3) existing3.destroy();

                    new Chart(showPurchaseDonut, {
                        type: 'doughnut',
                        data: {
                            labels,
                            datasets: [{
                                label: '단위: 원',
                                data,
                                backgroundColor: [
                                    'rgba(76, 175, 80, 0.7)',
                                    'rgba(33, 150, 243, 0.7)',
                                    'rgba(255, 193, 7, 0.7)',
                                    'rgba(244, 67, 54, 0.7)',
                                    'rgba(156, 39, 176, 0.7)'
                                ],
                                borderWidth: 1
                            }]
                        },
                        options: {
                            responsive: true,
                            cutout: '60%',
                            plugins: {
                                legend: { position: 'bottom' },
                                title: {
                                    display: true,
                                    text: '최근 1년 구매상품 TOP5 (단위: 원)'
                                }
                            }
                        }
                    });
                })
                .fail((err) => {
                    console.error('/api/dashboard/showChart/purchaseDonut 호출 중 에러 발생', err);
                });
        }

        // ✅ 4) 월별 판매 선 그래프 (폭 900px 컨테이너에 맞추기)
        if (showSaleLine) {
            $.get('/api/dashboard/showChart/saleLine')
                .done((res) => {
                    const labels = res.map(r => r.month);
                    const data   = res.map(r => r.total_price_sum);

                    const existing2 = Chart.getChart(showSaleLine);
                    if (existing2) existing2.destroy();

                    new Chart(showSaleLine, {
                        type: 'line',
                        data: {
                            labels,
                            datasets: [{
                                label: '월별 총 판매금액(원)',
                                data,
                                borderWidth: 2,
                                tension: 0.3,
                                fill: false
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,   // 🔹 컨테이너(900px / 340px)에 맞추기
                            plugins: {
                                legend: {
                                    position: 'top',
                                    onClick: (e) => e.stopPropagation()
                                },
                                title: { display: true }
                            },
                            scales: {
                                y: {
                                    beginAtZero: true,
                                    ticks: {
                                        callback: (value) =>
                                            value.toLocaleString() + ' 원'
                                    }
                                }
                            }
                        }
                    });
                })
                .fail((err) => {
                    console.error('/api/dashboard/showChart/saleLine 호출 중 에러 발생', err);
                });
        }

    }, 0);

    return html;
}

export async function dashboard_search_form() {
    return '';
}

export async function profit_search_form() {
    return '';
}
