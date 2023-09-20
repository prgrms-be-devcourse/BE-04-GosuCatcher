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
        document.getElementById('profile-pic').style.backgroundImage = `url(${imageUrl})`;
    } else {
        alert('프로필 이미지를 불러오는데 실패하였습니다.');
    }
}

async function fetchExpertProfile() {
    const token = localStorage.getItem('accessToken');

    const options = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    };

    const response = await fetch('/api/v1/experts', options);

    if (response.ok) {
        const expert = await response.json();
        document.getElementById('rating').innerText = expert.rating;
        document.getElementById('reviewCount').innerText = expert.reviewCount;
        document.getElementById('storeName').innerText = expert.storeName;
        document.getElementById('description').innerText = expert.description;
        document.getElementById('location').innerText = expert.location;
        document.getElementById('maxTravelDistance').innerText = expert.maxTravelDistance + 'km';

    } else {
        alert('Expert 프로필 정보를 불러오는데 실패하였습니다.');
    }
}

window.onload = () => {
    fetchProfileImage();
    fetchExpertProfile();
    loadSubItems();
};

async function loadSubItems() {
    try {
        const token = localStorage.getItem('accessToken');

        const options = {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        };

        const response = await fetch("/api/v1/experts/sub-items", options);
        const data = await response.json();
        displaySubItems(data.subItemsResponse);
    } catch (error) {
        console.error('서브 아이템 로딩 중 오류 발생:', error);
    }
}


function displaySubItems(subItems) {
    const container = document.getElementById('sub-items');
    container.innerHTML = "";

    subItems.forEach(subItem => {
        const button = document.createElement('button');

        button.className = "btn dashboard-btn btn-outline-primary";

        button.style.marginRight = '10px';

        button.innerText = subItem.name;

        const closeButton = document.createElement('span');
        closeButton.innerText = 'x';
        closeButton.onclick = () => removeSubItem(subItem.name);

        closeButton.style.marginLeft = '5px';
        closeButton.style.cursor = 'pointer';

        button.appendChild(closeButton);
        container.appendChild(button);
    });
}

async function removeSubItem(subItemName) {

    try {
        const token = localStorage.getItem('accessToken');
        const requestBody = {subItemName: subItemName};
        const response = await fetch("/api/v1/experts/sub-items", {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(requestBody),
        });

        if (response.status === 204) {
            loadSubItems();
        } else {
            console.error('서브 아이템 삭제 중 오류 발생:', await response.text());
        }
    } catch (error) {
        console.error('서브 아이템 삭제 중 오류 발생:', error);
    }
}

const modal = document.getElementById("myModal");
const btn = document.getElementById("add-sub-items");
const span = document.querySelector(".close");

btn.onclick = function () {
    modal.style.display = "block";
}

span.onclick = function () {
    modal.style.display = "none";
}

window.onclick = function (event) {
    if (event.target === modal) {
        modal.style.display = "none";
    }
}

async function addSubItemToExpert() {
    const token = localStorage.getItem('accessToken');
    const subItemName = document.getElementById('subItemSelect').value;

    const requestBody = {
        subItemName: subItemName
    };

    const response = await fetch("/api/v1/experts/sub-items", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(requestBody),
    });

    if (response.ok) {
        alert('서브 아이템이 성공적으로 추가되었습니다.');
        loadSubItems();
        modal.style.display = "none";
    } else {
        alert('서브 아이템 추가에 실패하였습니다.');
    }
}

function openEditModal() {
    fetchExpertEditProfile();
    const modal = document.getElementById('editModal');
    modal.style.display = "block";
}

function closeEditModal() {
    const modal = document.getElementById('editModal');
    modal.style.display = "none";
}

async function fetchExpertEditProfile() {
    const token = localStorage.getItem('accessToken');

    const options = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    };

    const response = await fetch('/api/v1/experts', options);

    if (response.ok) {
        const expert = await response.json();
        document.getElementById('storeNameInput').value = expert.storeName;
        document.getElementById('locationInput').value = expert.location;
        document.getElementById('maxTravelDistanceInput').value = expert.maxTravelDistance;
        document.getElementById('descriptionInput').value = expert.description;
    } else {
        alert('Expert 프로필 정보를 불러오는데 실패하였습니다.');
    }
}

async function submitEdit() {
    const token = localStorage.getItem('accessToken');

    const requestBody = {
        storeName: document.getElementById('storeNameInput').value,
        location: document.getElementById('locationInput').value,
        maxTravelDistance: parseInt(document.getElementById('maxTravelDistanceInput').value),
        description: document.getElementById('descriptionInput').value
    };

    const options = {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(requestBody)
    };

    const response = await fetch('/api/v1/experts', options);

    if (response.ok) {
        alert('회원정보 수정이 완료되었습니다.');
        closeEditModal();
    } else {
        alert('회원정보 수정에 실패하였습니다.');
    }
}

function uploadProfileImage() {
    const token = localStorage.getItem('accessToken');
    let inputFile = document.getElementById('__BVID__773');
    let formData = new FormData();
    formData.append('file', inputFile.files[0]);

    fetch('/api/v1/members/profile/images', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        body: formData
    }).then(response => response.json()).then(data => {
        alert('프로필 이미지가 성공적으로 업로드되었습니다.');
    }).catch(error => {
        alert('프로필 이미지 업로드 중 오류가 발생했습니다.');
    });
}

function setDefaultProfileImage() {
    const token = localStorage.getItem('accessToken');
    fetch('/api/v1/members/profile/images', {
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + token
        }
    }).then(response => {
        if (response.ok) {
            alert('프로필 이미지가 기본 이미지로 변경되었습니다.');
        } else {
            alert('프로필 이미지 변경 중 오류가 발생했습니다.');
        }
    });
}

document.addEventListener("DOMContentLoaded", function () {
    loadImages();
});

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
        img.style.width = '100px';
        img.style.height = '100px';
        img.style.objectFit = 'cover';
        img.style.borderRadius = '5px';

        const deleteButton = document.createElement('span');
        deleteButton.innerHTML = "x";
        deleteButton.style.position = 'absolute';
        deleteButton.style.top = '0';
        deleteButton.style.right = '0';
        deleteButton.style.cursor = 'pointer';
        deleteButton.onclick = function () {
            deleteImage(filename);
        };

        imageDiv.appendChild(img);
        imageDiv.appendChild(deleteButton);
        container.appendChild(imageDiv);
    });
}

function deleteImage(filename) {
    const token = localStorage.getItem('accessToken');

    fetch(`/api/v1/experts/images/${filename}`, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + token
        }
    })
        .then(response => {
            if (response.ok) {
                loadImages();
            } else {
                alert('이미지 삭제 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            alert('이미지 삭제 중 오류가 발생했습니다.');
        });
}

function openImageUploadModal() {
    $('#imageUploadModal').modal('show');
}

function uploadExpertImage() {
    const token = localStorage.getItem('accessToken');
    let inputFile = document.getElementById('__BVID__774');
    let formData = new FormData();
    formData.append('file', inputFile.files[0]);

    fetch('/api/v1/experts/images', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        body: formData
    }).then(response => response.json()).then(data => {
        alert('이미지가 성공적으로 업로드되었습니다.');
        $('#profileImageModal').modal('hide');
    }).catch(error => {
        alert('이미지 업로드 중 오류가 발생했습니다.');
    });
}
