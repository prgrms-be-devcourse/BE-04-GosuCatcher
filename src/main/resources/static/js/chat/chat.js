function getChatRooms() {
    let token = 'bearer ' + localStorage.getItem('accessToken');
    $.ajax({
        url: '/api/v1/chatting-rooms/members',
        method: 'GET',
        dataType: 'json',
        headers: {
            'Authorization': token,
        },
        success: function (data) {
            let chatRoomsContainer = $('#chatRoomsContainer');

            let chatRooms = data.chattingRoomsResponse;

            chatRooms.forEach(function (chatRoom) {
                let chatRoomHtml = `
                    <div class="card" style="width: auto">
                    <h3>채팅 정보</h3>
                        <p>ID: ${chatRoom.id}</p>
<!--<p>고수 ID:${chatRoom.memberEstimateResponse.expertId}</p> -->
                       <p>요청 서비스:${chatRoom.memberEstimateResponse.subItemResponse.name}</p>
                       <p>요청 지역:${chatRoom.memberEstimateResponse.location}</p>
                    </div>
                `;
                chatRoomsContainer.append(chatRoomHtml);
            });
        },
        error: function () {
            window.alert('회원 정보 조회에 실패했습니다. 새로고침 바랍니다.');
        }
    });
}
