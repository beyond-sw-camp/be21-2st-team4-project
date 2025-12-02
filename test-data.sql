-- Timedeal 테스트 데이터 삽입 SQL
-- 실행 방법: MariaDB에 접속 후 복사&붙여넣기

USE timedeal;

-- 1. 상품(Product) 데이터 삽입
INSERT INTO product (name, description, price, stock, image_url, status, created_at, updated_at) VALUES
('삼성 갤럭시 S24 Ultra', '최신 플래그십 스마트폰, 200MP 카메라', 1590000, 100, 'https://via.placeholder.com/400x200/007bff/ffffff?text=Galaxy+S24', 'ACTIVE', NOW(), NOW()),
('LG 그램 17인치 노트북', '초경량 대화면 노트북, 80Wh 배터리', 2190000, 50, 'https://via.placeholder.com/400x200/28a745/ffffff?text=LG+Gram', 'ACTIVE', NOW(), NOW()),
('애플 에어팟 프로 2세대', '액티브 노이즈 캔슬링, MagSafe 충전', 359000, 200, 'https://via.placeholder.com/400x200/dc3545/ffffff?text=AirPods+Pro', 'ACTIVE', NOW(), NOW()),
('다이슨 V15 무선청소기', '레이저 먼지 감지 기술', 899000, 30, 'https://via.placeholder.com/400x200/ffc107/ffffff?text=Dyson+V15', 'ACTIVE', NOW(), NOW()),
('소니 WH-1000XM5', '업계 최고 노이즈 캔슬링 헤드폰', 449000, 80, 'https://via.placeholder.com/400x200/17a2b8/ffffff?text=Sony+XM5', 'ACTIVE', NOW(), NOW()),
('샤오미 공기청정기 4 Pro', '초미세먼지 99.9% 제거', 329000, 120, 'https://via.placeholder.com/400x200/6610f2/ffffff?text=Xiaomi+Air', 'ACTIVE', NOW(), NOW()),
('아이패드 Air 5세대', 'M1 칩 탑재, Apple Pencil 지원', 929000, 60, 'https://via.placeholder.com/400x200/fd7e14/ffffff?text=iPad+Air', 'ACTIVE', NOW(), NOW()),
('닌텐도 스위치 OLED', '7인치 OLED 디스플레이', 398000, 150, 'https://via.placeholder.com/400x200/e83e8c/ffffff?text=Switch+OLED', 'ACTIVE', NOW(), NOW()),
('브레빌 바리스타 익스프레스', '가정용 에스프레소 머신', 799000, 25, 'https://via.placeholder.com/400x200/20c997/ffffff?text=Breville', 'ACTIVE', NOW(), NOW()),
('발뮤다 토스터', '스팀 기술로 빵 굽기의 완성', 289000, 40, 'https://via.placeholder.com/400x200/6c757d/ffffff?text=Balmuda', 'ACTIVE', NOW(), NOW());

-- 2. 프로모션(Promotion) 데이터 삽입
-- 현재 시간 기준으로 시작, 24시간 동안 진행되는 타임딜
INSERT INTO promotion (product_id, promotion_price, total_quantity, remaining_quantity, start_time, end_time, status, created_at, updated_at)
SELECT
    id,
    ROUND(price * 0.7),  -- 30% 할인
    CASE
        WHEN stock > 100 THEN 50
        WHEN stock > 50 THEN 30
        ELSE 20
    END,
    CASE
        WHEN stock > 100 THEN 50
        WHEN stock > 50 THEN 30
        ELSE 20
    END,
    NOW(),  -- 현재 시작
    DATE_ADD(NOW(), INTERVAL 24 HOUR),  -- 24시간 후 종료
    'ACTIVE',
    NOW(),
    NOW()
FROM product
WHERE status = 'ACTIVE'
LIMIT 8;  -- 처음 8개 상품만 타임딜 진행

-- 3. 향후 진행될 프로모션 (2일 후 시작)
INSERT INTO promotion (product_id, promotion_price, total_quantity, remaining_quantity, start_time, end_time, status, created_at, updated_at)
SELECT
    id,
    ROUND(price * 0.6),  -- 40% 할인
    15,
    15,
    DATE_ADD(NOW(), INTERVAL 2 DAY),
    DATE_ADD(NOW(), INTERVAL 3 DAY),
    'INACTIVE',
    NOW(),
    NOW()
FROM product
WHERE status = 'ACTIVE'
AND id NOT IN (SELECT product_id FROM promotion WHERE status = 'ACTIVE')
LIMIT 2;

-- 4. 테스트용 사용자 추가 (비밀번호: Test1234!)
INSERT INTO user (email, password, name, balance, status, created_at, updated_at) VALUES
('test@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '테스트유저', 1000000, 'ACTIVE', NOW(), NOW()),
('admin@admin.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '관리자', 5000000, 'ACTIVE', NOW(), NOW()),
('user1@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '김철수', 500000, 'ACTIVE', NOW(), NOW());

-- 5. 테스트용 관리자 추가
INSERT INTO admin (email, password, name, status, created_at, updated_at) VALUES
('admin@timedeal.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '시스템관리자', 'ACTIVE', NOW(), NOW());

-- 데이터 확인 쿼리
SELECT
    p.name AS product_name,
    p.price AS original_price,
    pr.promotion_price,
    ROUND((p.price - pr.promotion_price) / p.price * 100) AS discount_rate,
    pr.remaining_quantity,
    pr.total_quantity,
    pr.status,
    pr.start_time,
    pr.end_time
FROM promotion pr
JOIN product p ON pr.product_id = p.id
ORDER BY pr.created_at DESC;

-- 사용자 목록 확인
SELECT id, email, name, balance, status FROM user;

-- 프로모션 수 확인
SELECT
    status,
    COUNT(*) as count
FROM promotion
GROUP BY status;
