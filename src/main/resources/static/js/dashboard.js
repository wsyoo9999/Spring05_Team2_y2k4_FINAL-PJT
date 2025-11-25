// /static/js/dashboard.js

// 하드코딩 도넛 차트 출력용
// Chart.js는 main.html에서 CDN으로 미리 로드되어 있다고 가정
// <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

export async function show_charts(formData) {
    // 1) 테이블 영역(= #table) 안에 들어갈 HTML 문자열
    const html = `
      <div class="table-scroll-wrapper">
        <div style="padding: 20px; display: flex; justify-content: center;">
          <div style="max-width: 400px; width: 100%;">
            <h3 style="text-align:center; margin-bottom: 10px;">최근 1년 판매금액 TOP5</h3>
            <canvas id="test1"></canvas>
          </div>
          <div style="max-width: 400px; width: 100%;">
            <h3 style="text-align:center; margin-bottom: 10px;">대시보드 테스트2 도넛 차트</h3>
            <canvas id="test2"></canvas>
          </div>
        </div>
      </div>
    `;

    // 2) 차트 그리기는 setTimeout으로 예약
    //    (listClick에서 table.innerHTML = html 이 끝난 뒤에 실행되도록)
    setTimeout(() => {
        const ctx1 = document.getElementById('test1');
        const ctx2 = document.getElementById('test2');

        if (!ctx1 || !ctx2) {
            console.error('대시보드 차트용 캔버스를 찾을 수 없습니다.');
            return;
        }
        if (typeof Chart === 'undefined') {
            console.error('Chart.js가 로드되지 않았습니다.');
            return;
        }

        $.get('/api/dashboard/showChart/saleDonut')
            .done((res) => {
                console.log('💾 /api/dashboard/showChart/saleDonut 응답:', res);
                // res 예시: [{ stock_id:1, stock_name:'A상품', total_price_sum:123000 }, ...]
                const labels = res.map(r => r.stock_name);
                const data   = res.map(r => r.total_price_sum);

                // 기존 차트가 있으면 제거 (대시보드 다시 클릭할 때 대비)
                const existing1 = Chart.getChart(ctx1);
                if (existing1) existing1.destroy();

                new Chart(ctx1, {
                    type: 'doughnut',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: data,
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
                        cutout: '60%', // 도넛 구멍 크기
                        plugins: {
                            legend: { position: 'bottom' },
                        }
                    }
                });
            })
            .fail((err) => {
                console.error('/showChart 호출 중 에러 발생', err);
            });

        new Chart(ctx2, {
            type: 'doughnut',
            data: {
                labels: ['D상품', 'E상품', 'F상품'],
                datasets: [{
                    data: [25, 35, 15],
                    backgroundColor: [
                        'rgba(75, 192, 192, 0.7)',
                        'rgba(153, 102, 255, 0.7)',
                        'rgba(255, 159, 64, 0.7)'
                    ]
                }]
            },
            options: {
                responsive: true,
                cutout: '60%',
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }, 0);

    // listClick에서 table.innerHTML = html; 할 수 있도록 문자열 반환
    return html;
}

// 대시보드 검색폼 (없으면 빈 문자열)
export async function dashboard_search_form() {
    return '';
}

// 혹시 data-table="profit" 으로 되어 있다면 대비용
export async function profit_search_form() {
    return '';
}
