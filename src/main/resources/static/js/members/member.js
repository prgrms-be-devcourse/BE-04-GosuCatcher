let index = {
    init: function () {
        $("#btn-join").on("click", () => {
            this.join();
        });
    },

    join: function () {
        let data = {
            name: $("#name").val(),
            email: $("#email").val(),
            password: $("#password").val(),
        }

        var name = $("#name").val();
        var email = $("#email").val();
        var password = $("#password").val();

        if (name.length === 0) {
            alert("아이디를 입력해 주세요");
            $("#name").focus();
            return false;
        }

        if (name.length < 2 || name.length > 20) {
            alert("아이디는 2~20자 사이의 영어만 사용해 주세요");
            $("#name").focus();
            return false;
        }

        if (email.length === 0) {
            alert("이메일을 입력해 주세요");
            $("#email").focus();
            return false;
        }

        if (password.length < 5 || password.length > 20) {
            alert("비밀번호는 5~20자 사이만 입력 가능합니다!");
            $("#password").focus();
            return false;
        }

        if (password.length === 0) {
            alert("비밀번호를 입력해 주세요");
            $("#password").focus();
            return false;
        }

        $.ajax({
            type: "POST",
            url: "/api/v1/members/signup",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (res) {
            if (res.status === 500) {
                alert("유저 등록에 실패하였습니다!");
            } else {
                alert("유저 등록 완료!🎉");
                location.href = "/gosu-catcher";
            }
        }).fail(function (error) {
            alert("양식에 맞게 정보를 기입해 주세요!!");
        });
    },
}

function getAccessToken() {
    return localStorage.getItem('accessToken');
}

function navigateToAuto(subItemId) {
    const accessToken = getAccessToken();

    if (accessToken) {
        const headers = new Headers({
            'Authorization': `Bearer ${accessToken}`,
        });

        fetch(`/gosu-catcher/hire/auto/${subItemId}`, {
            method: 'GET',
            headers: headers,
        })
            .then(response => {
                if (response.ok) {
                    window.location.href = response.url;
                } else {
                    console.error('에러:', response.status);
                }
            })
            .catch(error => {
                console.error('Fetch 에러:', error);
            });
    } else {
        console.error('AccessToken을 사용할 수 없습니다.');
    }
}

index.init();
