$(document).ready(function () {
    const searchForm = $("#searchForm");
    const searchInput = $("#searchInput");
    const accessToken = localStorage.getItem('accessToken');

    searchForm.submit(function (event) {
        event.preventDefault(); // 폼 기본 제출 동작 방지

        const keyword = searchInput.val();
        if (keyword) {
            $.ajax({
                type: "POST",
                url: "/api/v1/search",
                data: {keyword: keyword},
                beforeSend: function (xhr) {
                    // 헤더에 accessToken 추가
                    xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
                },
                success: function (response) {
                    const redirectUrl = "/gosu-catcher/items?mainItemName=" + encodeURIComponent(keyword);
                    window.location.href = redirectUrl;
                    console.log(response);
                },
                error: function (xhr, status, error) {
                    console.error(error);
                }
            });
        }
    });

    $("#searchInput").on("focus", function () {
        if ($("#popularKeywords").is(":empty")) {
            $.ajax({
                url: "/api/v1/search/popularity",
                method: "GET",
                success: function (response) {
                    var keywords = response.searchRankingList.map(function (keywordObj) {
                        return keywordObj.keyword;
                    });


                    var keywordsHtml = '<ul>';
                    keywordsHtml += '<strong>인기 검색어</strong>';
                    keywords.forEach(function (keyword) {
                        keywordsHtml += '<li><a href="#" class="keyword-link">' + keyword + '</a></li>';
                    });
                    keywordsHtml += '</ul>';

                    $("#popularKeywords").html(keywordsHtml);
                    $("#popularKeywords").slideDown();
                },
                error: function () {
                    console.error("인기 키워드를 가져오는 중 오류가 발생했습니다.");
                }
            });
        } else {
            $("#popularKeywords").slideDown();
        }
        $.ajax({
            url: "/api/v1/search",
            method: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
            },
            success: function (recentSearchList) {

                var keywords = recentSearchList.searchResponseList.map(function (keywordObj) {
                    return keywordObj.keyword;
                });

                var keywordsHtml = '<ul>';
                keywordsHtml += '<strong>최근 검색어</strong>';
                keywords.forEach(function (keyword) {
                    keywordsHtml += '<li><a href="#" class="keyword-link">' + keyword + '</a></li>';
                });
                keywordsHtml += '</ul>';

                $("#popularKeywords").append(keywordsHtml);
            },
            error: function () {
                console.error("최근 검색 목록을 가져오는 중 오류가 발생했습니다.");
            }
        });
    });

    $(document).on("click", ".recent-search-link", function (event) {
        event.preventDefault();
        var clickedKeyword = $(this).text();
        $("#searchInput").val(clickedKeyword);
        $("#popularKeywords").slideUp();
    });
});

$(document).on("click", ".keyword-link", function (event) {
    event.preventDefault();
    var clickedKeyword = $(this).text();
    $("#searchInput").val(clickedKeyword);
    $("#popularKeywords").slideUp();
});

$(document).on("click", function (event) {
    if (!$(event.target).closest("#popularKeywords").length && !$(event.target).is("#searchInput")) {
        $("#popularKeywords").slideUp();
    }
});
