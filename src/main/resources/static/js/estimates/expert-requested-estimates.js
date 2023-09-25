const cardContainer = document.getElementById('cardContainer');
const accessToken = localStorage.getItem("accessToken");

fetch("http://localhost:8080/api/v1/member-estimates/normal", {
    method: "GET",
    headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${accessToken}`
    }
}).then(response => {
    if (!response.ok) {
        alert('요청받은 견적 정보를 조회할 수 없습니다.');
    }

    return response.json();
}).then(data => {
    const memberEstimates = data.memberEstimates;

    if (memberEstimates.length === 0) {
        cardContainer.innerHTML = '<p class="no-estimates">요청받은 견적이 없습니다.</p>';
        return;
    }

    memberEstimates.forEach(data => {
        console.log(data);

        const card = document.createElement('div');
        card.classList.add('card');

        const title = document.createElement('h2');
        title.textContent = data.subItemResponse.name;

        const requestTime = document.createElement('p')
        requestTime.classList.add('requestTime-detail');
        const formattedDate = formatDate(data.preferredStartDate);
        requestTime.textContent = `요청 날짜 : ${formattedDate}`;

        const status = document.createElement('p');
        status.classList.add('status-detail');
        if (data.status === 'PENDING') {
            status.textContent = `진행 상황: 견적요청`;
        } else if (data.status === 'PROCEEDING') {
            status.textContent = `진행 상황: 상담진행`;
        } else if (data.status === 'FINISHED') {
            status.textContent = `진행 상황: 요청마감`;
        }

        const mainItem = document.createElement('p');
        mainItem.classList.add('mainItem-detail');
        mainItem.textContent = `${data.subItemResponse.mainItemName}`;

        const detailButton = document.createElement('button');
        detailButton.classList.add('detail-button');
        detailButton.textContent = '견적 보내기';

        const memberEstimateId = `${data.id}`;

        detailButton.addEventListener('click', () => {
            window.location.href = "http://localhost:8080/gosu-catcher/normal-response?memberEstimateId=" + memberEstimateId;
        });

        card.appendChild(title);
        card.appendChild(requestTime);
        card.appendChild(status);
        card.appendChild(mainItem);
        card.appendChild(detailButton);

        cardContainer.appendChild(card);
    });
});

function formatDate(inputDate) {
    const date = new Date(inputDate);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}/${month}/${day}`;
}
