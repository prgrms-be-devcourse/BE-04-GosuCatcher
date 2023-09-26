import {tokenValidation} from "./tokenValidCheck.js";

document.addEventListener("DOMContentLoaded", async function () {
    let isMemberLogin, memberEmail, memberName;

    try {
        [isMemberLogin, memberEmail, memberName] = await tokenValidation();

        // 네비게이션 메뉴 엘리먼트를 가져옴
        const loginLink = document.querySelector(".nav-link[href='/gosu-catcher/login']");
        const joinLink = document.querySelector(".nav-link[href='/gosu-catcher/joinForm']");
        const myInfoLink = document.querySelector(".nav-link[href='/gosu-catcher/joinForm']");

        // 로그인 및 회원가입 버튼을 삭제하고 로그아웃 버튼을 만듦
        if (isMemberLogin) {
            loginLink.style.display = "none";
            joinLink.style.display = "none";

            // 로그아웃 버튼 생성
            const logoutLink = document.createElement("a");
            logoutLink.href = "#"; // 로그아웃 링크의 동작을 정의해야 함
            logoutLink.className = "nav-link";
            logoutLink.textContent = "로그아웃";
            logoutLink.style.color = "black";

            // 로그아웃 버튼 클릭 시 로그아웃 처리
            logoutLink.addEventListener("click", async function () {
                try {
                    const accessToken = localStorage.getItem("accessToken");

                    const response = await fetch("http://localhost:8080/api/v1/members/logout", {
                        method: "DELETE",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${accessToken}`
                        }
                    });

                    if (response.status === 204) {
                        // 로컬 스토리지에서 accessToken 및 refreshToken 삭제
                        localStorage.removeItem("accessToken");
                        localStorage.removeItem("refreshToken");

                        alert("로그아웃 했습니다.");
                        // 로그아웃 후 메인 페이지로 이동
                        window.location.href = "/gosu-catcher";
                    }
                } catch (error) {
                    console.error("Logout Error:", error);
                }
            });

            myInfoLink.parentElement.insertAdjacentElement("afterend", logoutLink);
        }
    } catch (error) {
        console.error("Token Validation Error:", error);
    }
});
