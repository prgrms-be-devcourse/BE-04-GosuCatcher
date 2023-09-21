async function loadServiceModalItems() {
    const token = localStorage.getItem('accessToken');
    const options = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    };

    const response = await fetch('/api/v1/sub-items', options);
    const responseData = await response.json();
    console.log(responseData);

    if (response.ok) {
        const modalBody = document.getElementById("serviceModalBody");
        modalBody.innerHTML = "";

        responseData.subItemsResponse.forEach(item => {
            const serviceElement = document.createElement("div");
            serviceElement.classList.add("service-item");
            serviceElement.innerText = item.name;
            serviceElement.addEventListener("click", () => {
                updateUrlWithSelectedService(item.name);

                $('#serviceModal').modal('hide');
            });
            modalBody.appendChild(serviceElement);
        });
    } else {
        alert('서비스 항목들을 불러오는데 실패하였습니다.');
    }
}

let currentPage = 0;
let hasNext = true;


$('#serviceModal').on('show.bs.modal', loadServiceModalItems);

function updateUrlWithSelectedService(serviceName) {
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set("subItem", serviceName);
    window.history.pushState({}, "", currentUrl);
}

$('#locationModalBody').on('click', '.location-item', function() {
    const locationName = $(this).data('value');
    updateUrlWithSelectedLocation(locationName);
    $('#locationModal').modal('hide');
});

function updateUrlWithSelectedLocation(locationName) {
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set("location", locationName);
    window.history.pushState({}, "", currentUrl);
}

$('.dropdown-item').on('click', function(event) {
    event.preventDefault();
    const sortValue = $(this).data('sort');
    const sortParam = sortValue + ',desc';
    updateUrlWithSelectedSort(sortParam);
    requestExpertsData();
});

function updateUrlWithSelectedSort(sortValue) {
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set("sort", sortValue);
    window.history.pushState({}, "", currentUrl);
}

async function requestExpertsData() {

    currentPage = 0;
    hasNext = true;

    const url = new URL(window.location.href);
    const token = localStorage.getItem('accessToken');
    const options = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }

    };

    const response = await fetch('/api/v1/experts/search' + url.search, options);
    const responseData = await response.json();

    if (response.ok) {
        displayExperts(responseData.expertsResponse);
    } else {
        alert('전문가 정보를 불러오는데 실패하였습니다.');
    }
}

function displayExperts(experts) {
    const container = document.querySelector('.container');

    experts.forEach(expert => {
        const expertDiv = document.createElement('div');
        expertDiv.classList.add('expert-item');


        const imgElement = document.createElement('img');
        imgElement.src = expert.filename;
        imgElement.alt = `${expert.storeName}의 프로필 사진`;
        imgElement.style.width = '100px';
        expertDiv.appendChild(imgElement);

        expertDiv.innerHTML += `
            <h3><a href="/experts/${expert.id}">${expert.storeName}</a></h3>
            <p>위치: ${expert.location}</p>
            <p>최대 이동 거리: ${expert.maxTravelDistance}km</p>
            <p>설명: ${expert.description}</p>
            <p>평점: ${expert.rating}</p>
            <p>리뷰 개수: ${expert.reviewCount}</p>
        `;

        container.appendChild(expertDiv);
    });
}


window.addEventListener('scroll', async () => {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 10) {
        await fetchMoreData();
    }
});

async function fetchMoreData() {
    if (!hasNext) return;

    const url = new URL(window.location.href);
    url.searchParams.set("page", ++currentPage);

    const token = localStorage.getItem('accessToken');
    const options = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    };

    const response = await fetch('/api/v1/experts/search' + url.search, options);
    const responseData = await response.json();

    if (response.ok) {
        displayExperts(responseData.expertsResponse);
        hasNext = responseData.hasNext;
    } else {
        alert('전문가 정보를 불러오는데 실패하였습니다.');
    }
}
