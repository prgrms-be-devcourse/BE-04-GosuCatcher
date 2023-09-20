const cardContainer = document.getElementById('cardContainer');
const accessToken = localStorage.getItem("accessToken");
fetch("http://localhost:8080/api/v1/member-estimates/members", {
    method: "GET",
    headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${accessToken}`
    }
}).then(response => {
    if (!response.ok) {
        alert('받은 견적 정보를 조회할 수 없습니다.');
    }

    return response.json();
}).then(data => {
    const memberEstimates = data.memberEstimates;

    if (memberEstimates.length === 0) {
        cardContainer.innerHTML = '<p class="no-estimates">받은 견적이 없습니다.</p>';
        return;
    }

    memberEstimates.forEach(data => {
        const card = document.createElement('div');
        card.classList.add('card');

        const title = document.createElement('h2');
        title.textContent = data.title;

        const requestTime = document.createElement('p')
        requestTime.textContent = `요청 날짜: ${data.preferredStartDate}`;

        const status = document.createElement('p');
        status.textContent = `상태: ${data.status}`;

        const detailButton = document.createElement('button');
        detailButton.classList.add('detail-button');
        detailButton.textContent = '자세히 보기';
        detailButton.addEventListener('click', () => {
            // 자세한 정보 페이지로 이동하는 코드를 추가합니다.
            window.location.href = `detailPage.html?quote=${encodeURIComponent(data.title)}`;
        });

        card.appendChild(title);
        card.appendChild(requestTime);
        card.appendChild(status);
        card.appendChild(detailButton);

        cardContainer.appendChild(card);
    });
});
