$(document).ready(function () {
    loadUserInfo();
    loadUserImage();
    $('#withdrawLink').on('click', function (e) {
        e.preventDefault(); // 기본 동작(링크 이동)을 막음
        deleteMember();
    });
});

function modifyUserInfo() {
    const token = 'bearer ' + localStorage.getItem('accessToken');

    const path = '/api/v1/members/profile/';

    if (document.getElementById('inputPhoneNumber').value.length !== 11) {
        window.alert("핸드폰 번호를 숫자만 11자로 입력해주세요")
        return;
    }

    if (document.getElementById('inputPassword').value.length < 5) {
        window.alert("비밀번호를 5자 이상 입력해주세요")
        return;
    }


    const requestBody = JSON.stringify({
        name: $('#name').text(),
        password: document.getElementById('inputPassword').value,
        phoneNumber: document.getElementById('inputPhoneNumber').value,
    });

    const request = {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': token,
        },
        body: requestBody
    }


    fetch(path, request)
        .then(() => window.alert("수정되었습니다."))
        .catch(() => window.alert("조건에 맞게 다시 입력해주세요"));
}

function loadUserInfo() {
    let token = 'bearer ' + localStorage.getItem('accessToken');
    $.ajax({
        url: '/api/v1/members/profile/',
        method: 'GET',
        dataType: 'json',
        headers: {
            'Authorization': token,
        },
        success: function (data) {
            $('#name').text(data.name);
            $('#email').text(data.email);
            $('#phone').text(data.phoneNumber);
        },
        error: function () {
            window.alert('회원 정보 조회에 실패했습니다. 새로고침 바랍니다.')
        }
    });
}

function loadUserImage() {
    let token = 'bearer ' + localStorage.getItem('accessToken');
    $.ajax({
        url: '/api/v1/members/profile/images',
        method: 'GET',
        dataType: 'json',
        headers: {
            'Authorization': token,
        },
        success: function (data) {
            document.querySelector('img').src = data.filenames[0];
        },
        error: function () {
            window.alert('회원 프로필 이미지 조회에 실패했습니다. 새로고침 바랍니다.')
        }
    });
}

function setDefaultProfileImage() {
    const token = 'bearer ' + localStorage.getItem('accessToken');
    const defaultUrl = 'https://gosu-catcher.s3.ap-northeast-2.amazonaws.com/default.png';
    if (document.querySelector('img').src === defaultUrl) {
        return;
    }

    fetch('/api/v1/members/profile/images', {
        method: 'DELETE',
        headers: {
            'Authorization': token
        }
    }).then(response => {
        if (response.ok) {
            alert('프로필 이미지가 기본 이미지로 변경되었습니다.');
        } else {
            alert('프로필 이미지 변경 중 오류가 발생했습니다.');
        }
    });
}

function deleteMember() {
    let token = 'bearer ' + localStorage.getItem('accessToken');
    $.ajax({
        url: '/api/v1/members',
        method: 'DELETE',
        headers: {
            'Authorization': token,
        },
        success: function () {
            alert('완료되었습니다.')
            window.location.href = '/gosu-catcher';
        },
        error: function () {
            alert('탈퇴 중 오류가 발생했습니다. 재시도해 주시기를 바랍니다.');
        }
    });
}


function uploadProfileImage() {
    const token = 'bearer ' + localStorage.getItem('accessToken');
    var image = $('input[name="file"]').get(0).files[0];

    var formData = new FormData();
    formData.append('file', image);

    $.ajax({
        type: 'POST',
        url: '/api/v1/members/profile/images',
        processData: false,
        contentType: false,
        data: formData,
        headers: {
            "Authorization": token
        },
        success: function (json) {
            alert("등록되었습니다.");

        },
        error: function (xhr, status, error) {
            alert("이미지 등록에 실패했습니다." + error);
        }
    });
}
