-- members
INSERT INTO members (name, password, email, phone_number, is_deleted, role, refresh_token)
VALUES ('John Doe', 'password123', 'john.doe@exampdle.com', '+12345678940', false, 'ROLE_USER', 'refreshToken123'),
       ('Jane Smith', 'password456', 'jane.smith@exafmple.com', '+98765432210', false, 'ROLE_USER', 'refreshToken456'),
       ('Bob Brown', 'bobpass', 'bob.brown@exampdle.com', '+44455566266', false, 'ROLE_USER', 'refreshToken789'),
       ('Charlie Green', 'charliepass', 'charalie.green@examdple.com', '010-1212-1211', false, 'ROLE_USER',
        'refreshToken1011'),
       ('Charlie Green', 'charliepass', 'charlie.green@exampdle.com', '010-1212-1222', false, 'ROLE_USER',
        'refreshToken1011'),
       ('Charlie Green', 'charliepass', 'charlie.green@examdple.com', '010-1212-1213', false, 'ROLE_USER',
        'refreshToken1011'),
       ('Charlie Green', 'charliepass', 'charlie.green@dexamdple.com', '010-1212-1214', false, 'ROLE_USER',
        'refreshToken1011'),
       ('Charlie Green', 'charliepass', 'charlie.green@examplae.com', '010-1212-1215', false, 'ROLE_USER',
        'refreshToken1011'),
       ('Charlie Green', 'charliepass', 'charlie.green@exadmpdle.com', '010-1212-1216', false, 'ROLE_USER',
        'refreshToken1011'),
       ('Charlie Green', 'charliepass', 'charlie.green@eaxampdle.com', '010-1212-1217', false, 'ROLE_USER',
        'refreshToken1011');

-- main_items
INSERT INTO main_items (name, description, is_deleted)
VALUES ('청소', '집안 청소 서비스로, 창문 청소, 마루 청소, 방 청소, 정리정돈 등을 제공합니다.', false),
       ('알바', '다양한 알바 기회를 제공하는 서비스로, 카페 알바, 서점 알바, 음식점 알바, 영화관 알바 등을 찾을 수 있습니다.', false),
       ('개발', '웹 개발, 앱 개발, 게임 개발, 소프트웨어 개발 등 다양한 개발 서비스를 제공합니다.', false),
       ('레슨', '영어 레슨, 피아노 레슨, 첼로 레슨, 타악기 레슨, 포토샵 레슨, 영어 회화, 중국어 회화, 독일어 회화, 러시아어 회화 등의 레슨을 제공합니다.', false),
       ('심부름/도우미', '각종 심부름과 도우미 역할을 맡아주는 서비스로, 장보기, 의류 수선, 가정용품 수리, 애완동물 돌봄, 가사 도우미 등을 지원합니다.', false),
       ('비즈니스', '비즈니스와 관련된 다양한 컨설팅 서비스를 제공합니다. 마케팅 컨설팅, 재무 컨설팅, 비즈니스 플랜 작성, 스타트업 지원 등을 지원합니다.', false),
       ('건강/미용', '건강과 미용 관련 서비스를 제공합니다. 스파 마사지, 피트니스 트레이닝, 미용 서비스, 헤어 스타일링 등을 제공합니다.', false),
       ('자동차', '자동차 관련 서비스로, 자동차 정비, 자동차 세차, 차량 검사, 운전 교육 등을 제공합니다.', false),
       ('촬영/사운드', '사진 촬영, 동영상 촬영, 오디오 녹음, 영상 편집 등의 촬영과 사운드 서비스를 제공합니다.', false),
       ('기타', '다양한 기타 서비스를 제공합니다.', false);

-- sub_items
INSERT INTO sub_items (main_item_id, name, description, is_deleted)
VALUES (1, '화장실 청소', '화장실을 청소하는 서비스입니다. 화장실 내부 및 바닥 청소가 포함됩니다.', false),
       (1, '주방 청소', '주방을 청소하는 서비스입니다. 주방 카운터, 싱크대, 가전제품 등을 청소합니다.', false),
       (1, '욕실 청소', '욕실을 청소하는 서비스입니다. 욕실 내부, 샤워부스, 변기 등을 청소합니다.', false),
       (1, '이사 청소', '이사 전후에 집을 청소하는 서비스입니다. 모든 공간을 청소하여 새 집으로 옮겨가기 전까지 완벽히 정리합니다.', false),
       (1, '창문 청소', '창문과 유리문을 깨끗하게 청소하는 서비스입니다. 창문 유리와 프레임을 모두 포함합니다.', false),
       (1, '마루 청소', '집 내의 마루나 목재 바닥을 청소하는 서비스입니다. 마루의 먼지와 얼룩을 제거합니다.', false),
       (1, '방 청소', '방 하나를 청소하는 서비스입니다. 방의 모든 부분을 청소하여 깨끗하게 만듭니다.', false),
       (1, '정리정돈', '정리와 정돈 서비스입니다. 물건 정리, 수납장 정리 등을 포함합니다.', false),
       (2, '영어 과외 알바', '영어 과외 알바를 제공합니다. 학생들에게 영어 학습 지원을 해주는 서비스입니다.', false),
       (2, '베이커리 알바', '베이커리에서 일하는 알바생을 고용하는 서비스입니다. 빵과 디저트를 만들고 판매합니다.', false),
       (2, '목공소 알바', '목공소에서 일하는 알바생을 고용하는 서비스입니다. 목재 가구 및 제품 제작을 도와줍니다.', false),
       (2, '단기 알바', '단기 알바 기회를 제공합니다. 다양한 업무를 시도해보고 일 경험을 쌓을 수 있습니다.', false),
       (2, '카페 알바', '카페에서 일하는 알바생을 고용하는 서비스입니다. 커피와 음료 제조 및 서빙이 주 업무입니다.', false),
       (2, '서점 알바', '서점에서 일하는 알바생을 고용하는 서비스입니다. 책 판매 및 고객 서비스를 제공합니다.', false),
       (2, '음식점 알바', '음식점에서 일하는 알바생을 고용하는 서비스입니다. 주방 보조 및 서빙이 주 업무입니다.', false),
       (2, '영화관 알바', '영화관에서 일하는 알바생을 고용하는 서비스입니다. 영화 티켓 판매 및 상영 시스템 관리가 주 업무입니다.', false),
       (3, '웹 개발', '웹 개발 서비스를 제공합니다. 웹 사이트 및 애플리케이션 개발을 전문으로 합니다.', false),
       (3, '앱 개발', '앱 개발 서비스를 제공합니다. 모바일 애플리케이션 개발을 전문으로 합니다.', false),
       (3, '게임 개발', '게임 개발 서비스를 제공합니다. 컴퓨터 및 모바일 게임 개발을 전문으로 합니다.', false),
       (3, '소프트웨어 개발', '소프트웨어 개발 서비스를 제공합니다. 다양한 소프트웨어 솔루션을 개발합니다.', false),
       (3, '데이터베이스 개발', '데이터베이스 개발 서비스를 제공합니다. 데이터베이스 설계 및 관리를 전문으로 합니다.', false),
       (3, '프로그래밍 교육', '프로그래밍 교육 서비스를 제공합니다. 프로그래밍 및 코딩 교육을 진행합니다.', false),
       (4, '영어 레슨', '영어 학습 레슨을 제공합니다. 언어 스킬 향상을 위한 수업을 제공합니다.', false),
       (4, '피아노 레슨', '피아노 레슨을 제공합니다. 음악 공연 및 연주 기술 향상을 돕습니다.', false),
       (4, '첼로 레슨', '첼로 레슨을 제공합니다. 현악기 연주 능력 향상을 위한 수업을 제공합니다.', false),
       (4, '타악기 레슨', '타악기 레슨을 제공합니다. 타악기 연주 기술을 향상시킵니다.', false),
       (4, '포토샵 레슨', '포토샵 레슨을 제공합니다. 그래픽 디자인 및 사진 편집 능력을 향상시킵니다.', false),
       (4, '영어 회화', '영어 회화 수업을 제공합니다. 실생활 대화 능력 향상을 위한 수업입니다.', false),
       (4, '중국어 회화', '중국어 회화 수업을 제공합니다. 중국어 실력 향상을 위한 수업입니다.', false),
       (4, '독일어 회화', '독일어 회화 수업을 제공합니다. 독일어 실력 향상을 위한 수업입니다.', false),
       (4, '러시아어 회화', '러시아어 회화 수업을 제공합니다. 러시아어 실력 향상을 위한 수업입니다.', false),
       (4, '미술 레슨', '미술 레슨을 제공합니다. 회화 및 조각 기술 향상을 위한 수업입니다.', false),
       (4, '댄스 레슨', '댄스 레슨을 제공합니다. 다양한 댄스 스타일을 배우고 춤을 추는 기술을 향상시킵니다.', false),
       (4, '요가 레슨', '요가 레슨을 제공합니다. 신체 건강과 스트레스 해소를 위한 요가 수업입니다.', false),
       (4, '요리 레슨', '요리 레슨을 제공합니다. 다양한 요리 기술을 배우고 요리 능력을 향상시킵니다.', false),
       (5, '장보기', '장보기 서비스를 제공합니다. 식료품 및 필수 생필품을 대신 구매해 드립니다.', false),
       (5, '의류 수선', '의류 수선 서비스를 제공합니다. 의류 수선 및 수리 작업을 수행합니다.', false),
       (5, '가정용품 수리', '가정용품 수리 서비스를 제공합니다. 가전제품 및 가정용품 수리를 수행합니다.', false),
       (5, '애완동물 돌봄', '애완동물 돌봄 서비스를 제공합니다. 반려동물의 케어 및 돌봄을 해 드립니다.', false),
       (5, '가사 도우미', '가사 도우미 서비스를 제공합니다. 가정 내 가사 업무를 도와드립니다.', false),
       (6, '마케팅 컨설팅', '마케팅 컨설팅 서비스를 제공합니다. 비즈니스의 마케팅 전략을 개선하고 지원합니다.', false),
       (6, '재무 컨설팅', '재무 컨설팅 서비스를 제공합니다. 재무 관리 및 투자 전략에 대한 조언을 제공합니다.', false),
       (6, '비즈니스 플랜 작성', '비즈니스 플랜 작성 서비스를 제공합니다. 비즈니스 계획서 작성을 지원합니다.', false),
       (6, '스타트업 지원', '스타트업 지원 서비스를 제공합니다. 스타트업 기업들을 지원하고 협력합니다.', false),
       (7, '스파 마사지', '스파 마사지 서비스를 제공합니다. 휴식과 피로 회복을 위한 마사지를 제공합니다.', false),
       (7, '피트니스 트레이닝', '피트니스 트레이닝 서비스를 제공합니다. 신체 컨디셔닝과 운동 지도를 제공합니다.', false),
       (7, '미용 서비스', '미용 서비스를 제공합니다. 헤어컷, 메이크업, 피부 관리 등 다양한 미용 서비스를 제공합니다.', false),
       (7, '헤어 스타일링', '헤어 스타일링 서비스를 제공합니다. 헤어 커팅, 염색 및 스타일링을 제공합니다.', false),
       (8, '자동차 정비', '자동차 정비 서비스를 제공합니다. 자동차 수리 및 정비 작업을 수행합니다.', false),
       (8, '자동차 세차', '자동차 세차 서비스를 제공합니다. 자동차 세차와 내부 청소를 진행합니다.', false),
       (8, '차량 검사', '차량 검사 서비스를 제공합니다. 자동차 안전 검사 및 정비를 지원합니다.', false),
       (8, '운전 교육', '운전 교육 서비스를 제공합니다. 운전 실력 향상을 위한 교육을 제공합니다.', false),
       (9, '사진 촬영', '사진 촬영 서비스를 제공합니다. 다양한 이벤트 및 포트레이트 촬영을 진행합니다.', false),
       (9, '동영상 촬영', '동영상 촬영 서비스를 제공합니다. 이벤트 비디오 및 프로모션 동영상 촬영을 지원합니다.', false),
       (9, '오디오 녹음', '오디오 녹음 서비스를 제공합니다. 음성 녹음 및 오디오 프로덕션을 지원합니다.', false),
       (9, '영상 편집', '영상 편집 서비스를 제공합니다. 영상 편집 및 후속 처리 작업을 수행합니다.', false);

-- experts
INSERT INTO experts (member_id, store_name, location, max_travel_distance, description, is_auto, rating, review_count,
                     is_deleted)
VALUES (1, 'Store A', 'Location A', 10, 'Description for Store A', false, 0.0, 0, false),
       (2, 'Store B', 'Location B', 15, 'Description for Store B', true, 0.0, 0, false),
       (3, 'Store C', 'Location C', 8, 'Description for Store C', false, 0.0, 0, false),
       (4, 'Store D', 'Location D', 12, 'Description for Store D', true, 0.0, 0, false),
       (5, 'Store E', 'Location E', 20, 'Description for Store E', false, 0.0, 0, false),
       (6, 'Store F', 'Location F', 7, 'Description for Store F', true, 0.0, 0, false),
       (7, 'Store G', 'Location G', 9, 'Description for Store G', false, 0.0, 0, false),
       (8, 'Store H', 'Location H', 18, 'Description for Store H', true, 0.0, 0, false),
       (9, 'Store I', 'Location I', 14, 'Description for Store I', false, 0.0, 0, false),
       (10, 'Store J', 'Location J', 6, 'Description for Store J', true, 0.0, 0, false);

-- member_estimates
INSERT INTO member_estimates (member_id, sub_item_id, location, preferred_start_date, detailed_description,
                              is_closed)
VALUES (1, 1, 'Location A', '2023-09-10 10:00:00', 'Description for Request 1', false),
       (2, 2, 'Location B', '2023-09-15 14:30:00', 'Description for Request 2', true),
       (3, 3, 'Location C', '2023-09-20 08:45:00', 'Description for Request 3', false),
       (4, 4, 'Location D', '2023-09-25 16:00:00', 'Description for Request 4', true),
       (5, 5, 'Location E', '2023-09-30 11:20:00', 'Description for Request 5', false),
       (6, 6, 'Location F', '2023-10-05 09:30:00', 'Description for Request 6', true),
       (7, 7, 'Location G', '2023-10-10 13:15:00', 'Description for Request 7', false),
       (8, 8, 'Location H', '2023-10-15 15:45:00', 'Description for Request 8', true),
       (9, 9, 'Location I', '2023-10-20 12:10:00', 'Description for Request 9', false),
       (10, 10, 'Location J', '2023-10-25 07:55:00', 'Description for Request 10', true);

-- buckets
INSERT INTO buckets (expert_id, member_id, is_deleted)
VALUES (1, 2, false),
       (2, 3, false),
       (3, 4, false),
       (4, 5, false),
       (5, 6, false),
       (6, 7, false),
       (7, 8, false),
       (8, 9, false),
       (9, 10, false),
       (10, 1, false);

-- expert_estimates
INSERT INTO expert_estimates (expert_id, member_estimate_id, sub_item_id, total_cost, activity_location,
                              description, is_deleted)
VALUES (1, 1, 1, 100, '서울특별시 강남구', 'Description for Estimate 1', false),
       (2, 2, 1, 150, '서울특별시 강남구', 'Description for Estimate 2', false),
       (3, 3, 1, 80, '서울특별시 도봉구', 'Description for Estimate 3', false),
       (4, 4, 1, 120, '서울특별시 강서구', 'Description for Estimate 4', false),
       (5, 5, 2, 200, '서울특별시 성동구', 'Description for Estimate 5', false),
       (6, 6, 2, 70, '서울특별시 동작구', 'Description for Estimate 6', false),
       (7, 7, 3, 90, '서울특별시 동작구', 'Description for Estimate 7', false),
       (8, 8, 4, 180, '서울특별시 동작구', 'Description for Estimate 8', false),
       (9, 9, 5, 140, '서울특별시 관악구', 'Description for Estimate 9', false),
       (10, 10, 4, 60, '서울특별시 관악구', 'Description for Estimate 10', false);

-- expertItem (mapping table)
INSERT INTO expert_items (expert_id, sub_item_id)
VALUES (1, 1),
       (1, 3),
       (2, 1),
       (3, 7),
       (4, 9),
       (6, 1);

-- reviews
INSERT INTO reviews (expert_id, member_id, sub_item_id, content, rating, is_deleted)
VALUES (1, 2, 1, 'Review for Expert 1', 4, false),
       (2, 3, 2, 'Review for Expert 2', 5, true),
       (3, 4, 3, 'Review for Expert 3', 3, false),
       (4, 5, 4, 'Review for Expert 4', 4, true),
       (5, 6, 5, 'Review for Expert 5', 2, false),
       (6, 7, 6, 'Review for Expert 6', 5, true),
       (7, 8, 7, 'Review for Expert 7', 4, false),
       (8, 9, 8, 'Review for Expert 8', 3, true),
       (9, 10, 9, 'Review for Expert 9', 5, false),
       (10, 1, 10, 'Review for Expert 10', 4, true);
