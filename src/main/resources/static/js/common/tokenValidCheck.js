// tokenValidCheck.js
export function tokenValidation() {
    return new Promise((resolve, reject) => {
        const accessToken = localStorage.getItem("accessToken");
        let isMemberLogin;
        let memberEmail;
        let memberName;

        if (accessToken === null) {
            return resolve([false, "guest", "guest"]);
        }

        fetch("http://localhost:8080/api/v1/members/profile", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${accessToken}`
            }
        }).then(response => {
            if (response.status === 200) {
                isMemberLogin = true;
                return response.json();
            }
            if (response.status === 401 && accessToken !== null) {
                // 액세스토큰이 만료된 경우이므로 리플래시 토큰을 들고
                // 토큰 재발급으로 간다.
                return resolve([false, "guest", "guest"]);
            }
        }).then(data => {
            memberEmail = data.email;
            memberName = data.name;
            resolve([isMemberLogin, memberEmail, memberName]); // 데이터를 resolve로 전달
        }).catch(error => {
            reject(error); // 에러를 reject로 전달
        });
    });
}
