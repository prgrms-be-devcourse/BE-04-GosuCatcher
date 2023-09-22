window.onload = () => {
    fetchProfileImage();
    fetchBackgroundImage();
    loadImages();
    fetchExpertProfile();
};

async function fetchBackgroundImage() {
    const token = localStorage.getItem('accessToken');

    const options = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    };

    const response = await fetch('/api/v1/members/profile/images', options);

    if (response.ok) {
        const data = await response.json();
        const imageUrl = data.filenames[0];
        document.getElementById('background').src = imageUrl;
    } else {
        alert('프로필 이미지를 불러오는데 실패하였습니다.');
    }
}

async function fetchProfileImage() {
    const token = localStorage.getItem('accessToken');

    const options = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    };
    const response = await fetch('/api/v1/members/profile/images', options);

    if (response.ok) {
        const data = await response.json();
        const imageUrl = data.filenames[0];
        document.getElementById('profile-pic').src = imageUrl;
    } else {
        alert('프로필 이미지를 불러오는데 실패하였습니다.');
    }
}

function loadImages() {
    const token = localStorage.getItem('accessToken');

    fetch('/api/v1/experts/images', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        }
    })
        .then(response => response.json())
        .then(data => {
            renderImages(data.filenames);
        })
        .catch(error => {
            alert('이미지 로딩 중 오류가 발생했습니다.');
        });
}

function renderImages(filenames) {
    const container = document.getElementById('imagesContainer');
    container.innerHTML = '';

    filenames.forEach(filename => {
        const imageDiv = document.createElement('div');
        imageDiv.className = "image-item";
        imageDiv.style.position = 'relative';
        imageDiv.style.marginRight = '10px';

        const img = document.createElement('img');
        img.src = filename;
        img.alt = "Image";
        img.style.width = '200px';
        img.style.height = '200px';
        img.style.objectFit = 'cover';
        img.style.borderRadius = '5px';

        imageDiv.appendChild(img);
        container.appendChild(imageDiv);

    });
}

function fetchReviews(expertId, serviceName){

}
