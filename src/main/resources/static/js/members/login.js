document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("login-form");

    loginForm.addEventListener("submit", function (e) {
        e.preventDefault();

        const formData = new FormData(loginForm);
        const formDataObject = {};
        formData.forEach((value, key) => {
            formDataObject[key] = value;
        });

        // JSON 데이터로 변환
        const jsonData = JSON.stringify(formDataObject);
        fetch("http://localhost:8080/api/v1/members/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: jsonData,
        }).then(response => {
            if (response.status === 200) {
                response.json().then(data => {
                    if (data.accessToken === "" || data.refreshToken === "") {
                        alert("로그인 성공, 하지만 빈 토큰을 받았습니다.");
                    } else {
                        const accessToken = data.accessToken;
                        const refreshToken = data.refreshToken;

                        localStorage.setItem("accessToken", accessToken);
                        localStorage.setItem("refreshToken", refreshToken);

                        alert("로그인 했습니다.");

                        window.location.href = "http://localhost:8080/gosu-catcher";
                    }
                });
            } else {
                alert("로그인 실패. 다시 시도하세요.");
            }
        }).catch(error => {
            console.error("오류 발생:", error);
            alert("로그인 중 오류가 발생했습니다.");
        });
    });
});
