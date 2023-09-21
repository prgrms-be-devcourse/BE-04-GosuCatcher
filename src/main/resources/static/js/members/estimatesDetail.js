const urlParams = new URLSearchParams(window.location.search);
const receivedId = urlParams.get('id');

fetch(`http://localhost:8080/member-estimates/${receivedId}`, {
    "Content-Type": "application/json",
    "Authorization": `Bearer ${accessToken}`
}).then(response => {
    if (!response.ok) {
        throw new Error('견적 데이터를 불러오는 도중 오류가 발생했습니다.');
    }

    return response.json();
}).then(data => {
    const expertCardsContainer = document.getElementById('userCards');

    if (data.size === 0) {
        const emptyResponse = document.createElement('p');
        emptyResponse.textContent = "받은 견적이 없습니다.";
        emptyResponse.style.fontSize = '30px';
        expertCardsContainer.appendChild(emptyResponse);

        return;
    }

    data.forEach(estimate => {
        const userCard = createExpertEstimateCard(estimate);
        expertCardsContainer.appendChild(userCard);
    });
}).catch(error => {
    console.error('데이터 가져오기 오류:', error);
});

function createExpertEstimateCard(expertData) {
    const userCard = document.createElement('div');
    userCard.classList.add('user-card');

    const profileImage = document.createElement('img');
    profileImage.src = expertData.profileImageSrc;
    profileImage.alt = '프로필 사진';

    const userDetails = document.createElement('div');
    userDetails.classList.add('user-details');

    const name = document.createElement('h2');
    name.textContent = expertData.name;

    const rating = document.createElement('p');
    rating.textContent = `평점: ${expertData.rating}`;

    const employment = document.createElement('p');
    employment.textContent = `고용횟수: ${expertData.employment}`;

    const location = document.createElement('p');
    location.textContent = `위치: ${expertData.location}`;

    const experience = document.createElement('p');
    experience.textContent = `경력사항: ${expertData.experience}`;

    const cost = document.createElement('p');
    cost.textContent = `견적 비용: ${expertData.cost}`;

    userCard.appendChild(profileImage);
    userDetails.appendChild(name);
    userDetails.appendChild(rating);
    userDetails.appendChild(employment);
    userDetails.appendChild(location);
    userDetails.appendChild(experience);
    userDetails.appendChild(cost);
    userCard.appendChild(userDetails);

    const buttons = document.createElement('div');
    buttons.classList.add('buttons');

    const quoteButton = document.createElement('button');
    quoteButton.classList.add('quote-button');
    quoteButton.textContent = '견적서 보기';

    const chatButton = document.createElement('button');
    chatButton.classList.add('chat-button');
    chatButton.textContent = '채팅하기';

    buttons.appendChild(quoteButton);
    buttons.appendChild(chatButton);
    userCard.appendChild(buttons);

    return userCard;
}
