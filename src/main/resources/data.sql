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
VALUES ('청소', 'Description for Item 1', false),
       ('알바', 'Description for Item 2', false),
       ('개발', 'Description for Item 3', false),
       ('레슨', 'Description for Item 4', false),
       ('심부름/도우미', 'Description for Item 5', false),
       ('비즈니스', 'Description for Item 6', false),
       ('건강/미용', 'Description for Item 7', false),
       ('자동차', 'Description for Item 8', false),
       ('촬영/사운드', 'Description for Item 9', false),
       ('기타', 'Description for Item 10', false);

-- sub_items
INSERT INTO sub_items (main_item_id, name, description, is_deleted)
VALUES (1, '화장실 청소', 'Description for SubItem A', false),
       (1, '주방 청소', 'Description for SubItem B', false),
       (1, '욕실 청소', 'Description for SubItem B', false),
       (1, '이사 청소', 'Description for SubItem B', false),
       (2, '영어 과외 알바', 'Description for SubItem C', false),
       (2, '베이커리 알바', 'Description for SubItem D', false),
       (2, '목공소 알바', 'Description for SubItem D', false),
       (2, '단기 알바', 'Description for SubItem D', false),
       (3, '웹 개발', 'Description for SubItem E', false),
       (3, '앱 개발', 'Description for SubItem F', false),
       (3, '게임 개발', 'Description for SubItem F', false),
       (3, '소프트웨어 개발', 'Description for SubItem F', false),
       (4, '영어 레슨', 'Description for SubItem G', false),
       (4, '피아노 레슨', 'Description for SubItem H', false),
       (4, '첼로 레슨', 'Description for SubItem H', false),
       (4, '타악기 레슨', 'Description for SubItem H', false),
       (4, '포토샵 레슨', 'Description for SubItem H', false),
       (4, '영어 회화', 'Description for SubItem I', false),
       (4, '중국어 회화', 'Description for SubItem J', false),
       (4, '독일어 회화', 'Description for SubItem J', false),
       (4, '러시아어 회화', 'Description for SubItem J', false);

-- experts
INSERT INTO experts (member_id, store_name, location, max_travel_distance, description, is_auto, rating, review_count, is_deleted)
VALUES (1, 'Store A', 'Location A', 10, 'Description for Store A', false, 0.0, 0, false),
       (2, 'Store B', 'Location B', 15, 'Description for Store B', true, 0.0, 0,false),
       (3, 'Store C', 'Location C', 8, 'Description for Store C', false, 0.0, 0,false),
       (4, 'Store D', 'Location D', 12, 'Description for Store D', true, 0.0, 0,false),
       (5, 'Store E', 'Location E', 20, 'Description for Store E', false, 0.0, 0,false),
       (6, 'Store F', 'Location F', 7, 'Description for Store F', true, 0.0, 0,false),
       (7, 'Store G', 'Location G', 9, 'Description for Store G', false, 0.0, 0,false),
       (8, 'Store H', 'Location H', 18, 'Description for Store H', true, 0.0, 0,false),
       (9, 'Store I', 'Location I', 14, 'Description for Store I', false, 0.0, 0,false),
       (10, 'Store J', 'Location J', 6, 'Description for Store J', true, 0.0, 0,false);

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
INSERT INTO buckets (expert_id, member_id,is_deleted)
VALUES (1, 2,false),
       (2, 3,false),
       (3, 4,false),
       (4, 5,false),
       (5, 6,false),
       (6, 7,false),
       (7, 8,false),
       (8, 9,false),
       (9, 10,false),
       (10, 1,false);

-- expert_estimates
INSERT INTO expert_estimates (expert_id, member_estimate_id, sub_item_id, total_cost, activity_location,
                              description, is_deleted)
VALUES (1, 1, 1, 100, '서울시 강남구', 'Description for Estimate 1', false),
       (2, 2, 1, 150, '서울시 강남구', 'Description for Estimate 2', false),
       (3, 3, 1, 80, '서울시 도봉구', 'Description for Estimate 3', false),
       (4, 4, 1, 120, '서울시 강서구', 'Description for Estimate 4', false),
       (5, 5, 2, 200, '서울시 성동구', 'Description for Estimate 5', false),
       (6, 6, 2, 70, '서울시 동작구', 'Description for Estimate 6', false),
       (7, 7, 3, 90, '서울시 동작구', 'Description for Estimate 7', false),
       (8, 8, 4, 180, '서울시 동작구', 'Description for Estimate 8', false),
       (9, 9, 5, 140, '서울시 관악구', 'Description for Estimate 9', false),
       (10, 10, 4, 60, '서울시 관악구', 'Description for Estimate 10', false);

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
