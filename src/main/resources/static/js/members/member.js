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

index.init();
