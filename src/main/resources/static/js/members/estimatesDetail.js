// 모달 열기
function openModal(expertData) {
    const modal = document.getElementById('myModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalContent = document.getElementById('modalContent');
    modalTitle.textContent = expertData.memberEstimate.subItemResponse.name;
    modalContent.innerHTML
        = `
        <h3>${expertData.expert.storeName}</h3>
        <p>평점: ${expertData.expert.rating}</p>
        <p>위치: ${expertData.expert.location}</p>
        <p>경력사항: ${expertData.expert.description}</p>
        <h3>견적</h3>
        <p>예상금액: ${expertData.totalCost}원</p>
        <h3>견적 설명</h3>
        <p>${expertData.description}</p>
        `;

    modal.style.display = 'block';
}

// 모달 닫기
function closeModal() {
    const modal = document.getElementById('myModal');
    modal.style.display = 'none';
}

// 닫기 버튼과 모달 외부 클릭 시 모달 닫기
const closeButtons = document.querySelectorAll('.close');
closeButtons.forEach(button => {
    button.addEventListener('click', closeModal);
});

window.addEventListener('click', (event) => {
    const modal = document.getElementById('myModal');
    if (event.target === modal) {
        closeModal();
    }
});

const urlParams = new URLSearchParams(window.location.search);
const receivedId = urlParams.get('id');
const receivedPreferredDate = urlParams.get('date');
const receivedSubItemId = urlParams.get('subItemId');

const accessToken = localStorage.getItem("accessToken");

fetch(`http://localhost:8080/api/v1/sub-items/${receivedSubItemId}`, {
        method: "GET",
        headers:
            {
                "Authorization": `Bearer ${accessToken}`
            },
    }
).then(response => {
    if (!response.ok) {
        throw new Error('견적 데이터를 불러오는 도중 오류가 발생했습니다.');
    }

    return response.json();
}).then(data => {
    const subItemTitle = document.querySelector('.sub-item-name');
    const preferredDate = document.querySelector('.preferred-date');

    subItemTitle.textContent = data.name;
    preferredDate.textContent = receivedPreferredDate;

}).catch(error => {
    console.log(error);
})

fetch(`http://localhost:8080/api/v1/expert-estimates/member-estimates/${receivedId}`, {
    method: "GET",
    headers: {
        "Authorization": `Bearer ${accessToken}`
    },
}).then(response => {
    if (!response.ok) {
        throw new Error('견적 데이터를 불러오는 도중 오류가 발생했습니다.');
    }

    return response.json();
}).then(data => {
    const expertCardsContainer = document.getElementById('userCards');

    if (data.length === 0) {
        const emptyResponse = document.createElement('p');
        emptyResponse.textContent = "받은 견적이 없습니다.";
        emptyResponse.style.fontSize = '30px';
        expertCardsContainer.appendChild(emptyResponse);

        return;
    }

    const list = JSON.parse(JSON.stringify(data)).expertEstimateResponseList;
    list.forEach(estimate => {
        console.log(estimate);
        const userCard = createExpertEstimateCard(estimate);
        expertCardsContainer.appendChild(userCard);
    });
}).catch(error => {
    console.error('데이터 가져오기 오류:', error);
});

function createExpertEstimateCard(expertData) {
    const userCard = document.createElement('div');
    userCard.classList.add('user-card');
    const buttons = document.createElement('div');
    buttons.classList.add('buttons');

    fetchExpertImageData()
        .then(result => {
            const profileImage = document.createElement('img');
            profileImage.src = 'https://gosu-catcher.s3.ap-northeast-2.amazonaws.com/default.png';
            profileImage.alt = '프로필 사진';

            const userDetails = document.createElement('div');
            userDetails.classList.add('user-details');

            const name = document.createElement('h2');
            name.textContent = expertData.expert.storeName;

            const rating = document.createElement('p');
            rating.textContent = `평점: ${expertData.expert.rating}`;

            const location = document.createElement('p');
            location.textContent = `위치: ${expertData.expert.location}`;

            const experience = document.createElement('p');
            experience.textContent = `경력사항: ${expertData.expert.description}`;

            const cost = document.createElement('p');
            cost.textContent = `견적 비용: ${expertData.totalCost}원`;

            userCard.appendChild(profileImage);
            userDetails.appendChild(name);
            userDetails.appendChild(rating);
            userDetails.appendChild(location);
            userDetails.appendChild(experience);
            userDetails.appendChild(cost);
            userCard.appendChild(userDetails);

            const quoteButton = document.createElement('button');
            quoteButton.classList.add('quote-button');
            quoteButton.textContent = '견적서 보기';
            quoteButton.addEventListener('click', () => {
                openModal(expertData);
            })

            const chatButton = document.createElement('button');
            chatButton.classList.add('chat-button');
            chatButton.textContent = '채팅하기';

            buttons.appendChild(quoteButton);
            buttons.appendChild(chatButton);
            userCard.appendChild(buttons);
        })
        .catch(error => {
            console.log("오류:", error);
        });

    return userCard;
}

async function fetchExpertImageData() {
    return new Promise(async (resolve, reject) => {
        try {
            const response = await fetch("http://localhost:8080/api/v1/members/profile/images", {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${accessToken}`
                },
            });

            if (!response.ok) {
                throw new Error('고수 이미지를 불러오는 도중 오류가 발생했습니다.');
            }

            const data = await response.json();
            const imageUrl = data.filenames[0];

            resolve(imageUrl);
        } catch (error) {
            reject(error);
        }
    });
}
