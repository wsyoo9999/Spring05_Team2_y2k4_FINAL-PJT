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
            <h3 style="text-align:center; margin-bottom: 10px;">대시보드 테스트 도넛 차트</h3>
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
        // const canvas = document.getElementById('dashboardDonutChart');
        // if (!canvas) {
        //     console.error('dashboardDonutChart 캔버스를 찾을 수 없습니다.');
        //     return;
        // }
        // if (typeof Chart === 'undefined') {
        //     console.error('Chart.js가 로드되지 않았습니다.');
        //     return;
        // }

        const ctx1 = document.getElementById('test1');
        const ctx2 = document.getElementById('test2');


        // 하드코딩된 예시 데이터
        const labels = ['A 상품', 'B 상품', 'C 상품'];
        const data = [30, 15, 25];

        new Chart(ctx1, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    label: '테스트 비율',
                    data: data,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.7)',
                        'rgba(54, 162, 235, 0.7)',
                        'rgba(255, 206, 86, 0.7)',
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                cutout: '60%', // 도넛 구멍 크기
                plugins: {
                    legend: {
                        position: 'bottom'
                    },
                    title: {
                        display: true,
                        text: '하드코딩 도넛 차트 (Dashboard 테스트)'
                    }
                }
            }
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
                plugins: { legend: { position: 'bottom' } }
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
