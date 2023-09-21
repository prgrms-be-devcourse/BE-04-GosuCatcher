document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("login-form");

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const formData = {
            name: form.querySelector("#name").value,
            email: form.querySelector("#email").value,
        };
        const jsonData = JSON.stringify(formData);

        // Fetch API를 사용하여 서버에 POST 요청을 보냅니다.
        fetch("http://localhost:8080/api/v1/members/recovery/password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: jsonData,
        }).then(response => {
            if (!response.ok) {
                alert("회원정보가 일치하지 않습니다.")
            } else {
                alert("이메일로 비밀번호를 전송했습니다.")
            }

            return response.json();
        });
    });
});
