-- Timedeal 테스트 데이터 삽입 SQL
-- 실행 방법: MariaDB에 접속 후 복사&붙여넣기

USE timedeal;

-- ============================================
-- 0. 먼저 관리자 추가 (Product가 admin_id를 필요로 함)
-- ============================================
-- 테이블명: admins
-- 컬럼: email(length=10!), password(length=10!), company(length=10!), entity_status
INSERT INTO admins (email, password, company, entity_status, created_at, updated_at) VALUES
('admin@ad', 'Pass1234!', 'TestCo', 'ACTIVE', NOW(), NOW());

-- 방금 생성한 관리자 ID 가져오기
SET @admin_id = LAST_INSERT_ID();

-- ============================================
-- 1. 상품(Product) 데이터 삽입
-- ============================================
-- 테이블명: products
-- 컬럼: name, description, image_url, price, category, admin_id, entity_status
INSERT INTO products (name, description, image_url, price, category, admin_id, entity_status, created_at, updated_at) VALUES
('갤럭시 S24', '플래그십 스마트폰', 'https://via.placeholder.com/400x200/007bff/ffffff?text=Galaxy+S24', 1590000, '전자제품', @admin_id, 'ACTIVE', NOW(), NOW()),
('LG 그램', '초경량 노트북', 'https://via.placeholder.com/400x200/28a745/ffffff?text=LG+Gram', 2190000, '전자제품', @admin_id, 'ACTIVE', NOW(), NOW()),
('에어팟 프로', '무선 이어폰', 'https://via.placeholder.com/400x200/dc3545/ffffff?text=AirPods', 359000, '전자제품', @admin_id, 'ACTIVE', NOW(), NOW()),
('다이슨 V15', '무선청소기', 'https://via.placeholder.com/400x200/ffc107/ffffff?text=Dyson', 899000, '가전제품', @admin_id, 'ACTIVE', NOW(), NOW()),
('소니 XM5', '노이즈캔슬링 헤드폰', 'https://via.placeholder.com/400x200/17a2b8/ffffff?text=Sony', 449000, '전자제품', @admin_id, 'ACTIVE', NOW(), NOW()),
('샤오미 공기청정기', '공기청정기', 'https://via.placeholder.com/400x200/6610f2/ffffff?text=Xiaomi', 329000, '가전제품', @admin_id, 'ACTIVE', NOW(), NOW()),
('아이패드 Air', '태블릿 PC', 'https://via.placeholder.com/400x200/fd7e14/ffffff?text=iPad', 929000, '전자제품', @admin_id, 'ACTIVE', NOW(), NOW()),
('닌텐도 스위치', '게임 콘솔', 'https://via.placeholder.com/400x200/e83e8c/ffffff?text=Switch', 398000, '게임', @admin_id, 'ACTIVE', NOW(), NOW()),
('브레빌', '에스프레소 머신', 'https://via.placeholder.com/400x200/20c997/ffffff?text=Breville', 799000, '가전제품', @admin_id, 'ACTIVE', NOW(), NOW()),
('발뮤다', '토스터기', 'https://via.placeholder.com/400x200/6c757d/ffffff?text=Balmuda', 289000, '가전제품', @admin_id, 'ACTIVE', NOW(), NOW());

-- ============================================
-- 2. 프로모션(Promotion) 데이터 삽입
-- ============================================
-- 테이블명: promotions
-- 컬럼: admin_id, product_id, sale_price, discount_rate, start_time, end_time,
--       total_quantity, sold_quantity, promotion_status, entity_status
-- 현재 시간 기준으로 시작, 24시간 동안 진행되는 타임딜 (8개)
INSERT INTO promotions (admin_id, product_id, sale_price, discount_rate, start_time, end_time, total_quantity, sold_quantity, promotion_status, entity_status, created_at, updated_at)
SELECT
    @admin_id,
    id,
    ROUND(price * 0.7),  -- 30% 할인된 가격
    30.0,  -- 할인율 30%
    NOW(),  -- 현재 시작
    DATE_ADD(NOW(), INTERVAL 24 HOUR),  -- 24시간 후 종료
    50,  -- 총 수량
    0,   -- 판매된 수량
    'ACTIVE',  -- 프로모션 상태
    'ACTIVE',  -- 엔티티 상태
    NOW(),
    NOW()
FROM products
WHERE entity_status = 'ACTIVE'
LIMIT 8;

-- 향후 진행될 프로모션 (2일 후 시작, 2개)
INSERT INTO promotions (admin_id, product_id, sale_price, discount_rate, start_time, end_time, total_quantity, sold_quantity, promotion_status, entity_status, created_at, updated_at)
SELECT
    @admin_id,
    id,
    ROUND(price * 0.6),  -- 40% 할인된 가격
    40.0,  -- 할인율 40%
    DATE_ADD(NOW(), INTERVAL 2 DAY),
    DATE_ADD(NOW(), INTERVAL 3 DAY),
    30,
    0,
    'INACTIVE',  -- 아직 시작 안함
    'ACTIVE',
    NOW(),
    NOW()
FROM products
WHERE entity_status = 'ACTIVE'
AND id NOT IN (SELECT product_id FROM promotions WHERE promotion_status = 'ACTIVE')
LIMIT 2;

-- ============================================
-- 3. 테스트용 사용자 추가
-- ============================================
-- 테이블명: users
-- 컬럼: email(length=10!), password(length=10!), money, name, total_saved, entity_status
-- 주의: email과 password 길이가 10으로 제한되어 있음!
-- 비밀번호: Test1234! (실제로는 BCrypt 해시 필요하지만 여기서는 평문)
INSERT INTO users (email, password, money, name, total_saved, entity_status, created_at, updated_at) VALUES
('test@te', 'Test1234!', 1000000, '테스트', 0, 'ACTIVE', NOW(), NOW()),
('admin@ad', 'Test1234!', 5000000, '관리자', 0, 'ACTIVE', NOW(), NOW()),
('user@user', 'Test1234!', 500000, '유저1', 0, 'ACTIVE', NOW(), NOW());

-- ============================================
-- 데이터 확인 쿼리
-- ============================================

-- 프로모션 목록 확인 (상품 정보 포함)
SELECT
    pr.id AS promotion_id,
    p.name AS product_name,
    p.price AS original_price,
    pr.sale_price,
    pr.discount_rate,
    pr.total_quantity,
    pr.sold_quantity,
    pr.promotion_status,
    pr.start_time,
    pr.end_time
FROM promotions pr
JOIN products p ON pr.product_id = p.id
ORDER BY pr.created_at DESC;

-- 사용자 목록 확인
SELECT id, email, name, money, total_saved FROM users;

-- 관리자 목록 확인
SELECT id, email, company FROM admins;

-- 프로모션 상태별 개수
SELECT
    promotion_status,
    COUNT(*) as count
FROM promotions
GROUP BY promotion_status;
