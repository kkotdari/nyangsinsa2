-- 프로덕트 테이블 --
CREATE TABLE PRODUCT(
P_NO INT PRIMARY KEY AUTO_INCREMENT COMMENT '상품번호', 
P_NM VARCHAR(50) NOT NULL COMMENT '상품이름',
P_CATEGORY VARCHAR(20) NOT NULL COMMENT '카테고리',
P_PRICE INT NOT NULL COMMENT '가격',
P_AMT INT NOT NULL COMMENT '재고',
P_DETAIL VARCHAR(3000) NOT NULL DEFAULT ' ' COMMENT '상품 상세 설명',
DC_PERCENT INT COMMENT '상품 할인율'
) COMMENT '상품';

ALTER TABLE PRODUCT AUTO_INCREMENT = 100;



-- 멤버 테이블 --
CREATE TABLE MEMBER(
M_NO INT  PRIMARY KEY AUTO_INCREMENT COMMENT '회원번호',
M_ID VARCHAR(20) NOT NULL UNIQUE COMMENT '회원 아이디',
M_PW VARCHAR(20) NOT NULL COMMENT '회원 패스워드',
M_NM VARCHAR(20) NOT NULL COMMENT '회원 이름',
CAT_NM VARCHAR(20) COMMENT '고양이 이름',
PHONE_NO VARCHAR(12) NOT NULL COMMENT '회원 전화번호',
ROLE VARCHAR(20) DEFAULT 'MEMBER' COMMENT '등급',
WARN_CNT INT DEFAULT 0 COMMENT '경고횟수',
POST_NO VARCHAR(6) COMMENT '우편번호',
ADDRESS1 VARCHAR(100) COMMENT '기본주소',
ADDRESS2 VARCHAR(100) COMMENT '상세주소'
) COMMENT '회원';

ALTER TABLE `MEMBER` AUTO_INCREMENT = 100;


-- 오더 테이블 --
CREATE TABLE `ORDER` (
O_NO INT PRIMARY KEY AUTO_INCREMENT COMMENT '주문번호',
M_NO INT NOT NULL COMMENT '주문자 회원번호',
O_DT DATETIME DEFAULT (current_time) NOT NULL COMMENT '주문 날짜',
RCV_NM VARCHAR(30) NOT NULL COMMENT '수령인 이름',
RCV_PHONE_NO VARCHAR(12) NOT NULL COMMENT '수령인 전화번호',
RCV_ADDRESS VARCHAR(100) NOT NULL COMMENT '받는 사람 주소',
O_PAY VARCHAR(30) NOT NULL COMMENT '결제 방법'
) COMMENT '주문' ;

ALTER TABLE `ORDER` AUTO_INCREMENT = 100;


-- 오더디테일 테이블 --
CREATE TABLE ORDER_DETAIL (
OD_NO INT PRIMARY KEY AUTO_INCREMENT COMMENT '상세주문번호',
O_NO INT NOT NULL COMMENT '주문번호',
P_NO INT NOT NULL COMMENT '상품번호',
OD_CNT INT NOT NULL COMMENT '주문개수',
OD_PRICE INT NOT NULL COMMENT '상세 주문 금액'
) COMMENT '주문 상세';

ALTER TABLE ORDER_DETAIL AUTO_INCREMENT = 100;

-- 리뷰 테이블 --
CREATE TABLE REVIEW (
R_NO INT PRIMARY KEY AUTO_INCREMENT COMMENT '리뷰번호',
P_NO INT NOT NULL COMMENT '상품번호번호',
M_NO INT NOT NULL COMMENT '리뷰 작성자',
R_CONTENT VARCHAR(1000) NOT NULL COMMENT '리뷰 내용',
R_DT DATETIME DEFAULT (current_time) NOT NULL COMMENT '리뷰 작성일',
R_RATE INT NOT NULL COMMENT '리뷰 별점'
) COMMENT '리뷰';

ALTER TABLE REVIEW AUTO_INCREMENT = 100;


-- 이미지 테이블 --
CREATE TABLE IMAGE (
I_NO INT PRIMARY KEY AUTO_INCREMENT COMMENT'이미지 PK',
TARGET_NO INT NOT NULL COMMENT '게시판 혹은 상품 데이터의 PK',
TYPE_NO INT NOT NULL COMMENT '이미지 사용하는 테이블 구분',
I_NM VARCHAR(500) NOT NULL COMMENT '이미지 파일 이름'
) COMMENT '이미지';

ALTER TABLE IMAGE AUTO_INCREMENT = 100;


DROP TABLE PRODUCT;

DROP TABLE MEMBER;

DROP TABLE `ORDER`;

DROP TABLE ORDER_DETAIL;

DROP TABLE REVIEW;

DROP TABLE IMAGE;



/*
 * 고양이 게시판 테이블
 */
CREATE TABLE BOARD (
	B_NO INT PRIMARY KEY AUTO_INCREMENT COMMENT '글 번호',
	M_NO INT NOT NULL COMMENT '글 작성자 번호', -- FK
	B_TITLE VARCHAR(60) NOT NULL COMMENT '글 제목',
	B_CONTENT VARCHAR(4000) NOT NULL COMMENT '글 내용',
	B_DATE DATETIME DEFAULT (current_time) COMMENT '글 작성일',
	STATUS INT NOT NULL DEFAULT 1 COMMENT '글 상태',
	B_VIEW INT NOT NULL DEFAULT 0 COMMENT '조회수'
-- 	FOREIGN KEY (M_NO) references MEMBER(M_NO)
) COMMENT '고양이 자랑 게시판';

ALTER TABLE BOARD AUTO_INCREMENT = 100;

/*
 * 댓글 테이블
 */
CREATE TABLE REPLY (
	RE_NO INT PRIMARY KEY AUTO_INCREMENT COMMENT '댓글 번호',
	B_NO INT NOT NULL COMMENT '글 번호', -- FK 추가
	M_NO INT NOT NULL COMMENT '댓글 작성자 번호', -- FK 추가
	PARENT_NO INT COMMENT '부모 댓글의 RE_NO',
	RE_DATE DATETIME DEFAULT (current_time) COMMENT '댓글 작성일',
	RE_CONTENT VARCHAR(500) NOT NULL COMMENT '댓글 내용',
	STATUS INT NOT NULL DEFAULT 1 COMMENT '댓글 상태',
	RE_STEP INT NOT NULL COMMENT '댓글 계층',
	CONSTRAINT RE_STEP_CHECK CHECK(RE_STEP BETWEEN 1 AND 3) -- 계층 컬럼에 1, 2, 3 밖에 못 넣음
) COMMENT '고양이 게시판 - 댓글';

ALTER TABLE REPLY AUTO_INCREMENT = 100;


/*
 * 좋아요 테이블
 */
CREATE TABLE BLIKE (
	LK_NO INT PRIMARY KEY AUTO_INCREMENT COMMENT '좋아요 번호(PK)',
	B_NO INT NOT NULL COMMENT '글 번호', -- FK 추가
	M_NO INT NOT NULL COMMENT '회원 번호' -- FK 추가
) COMMENT '고양이 게시판 - 좋아요';


/*
 * 신고 테이블
 */
CREATE TABLE REPORT (
	RP_NO INT PRIMARY KEY AUTO_INCREMENT COMMENT '신고 번호(PK)',
	TARGET_NO INT NOT NULL COMMENT '글/댓글의 PK', -- FK 추가
	RP_STEP INT NOT NULL COMMENT '글/댓글 구분', -- FK 추가
	M_NO INT NOT NULL COMMENT '신고 당한 회원 번호', -- FK 추가
	RP_M_NO INT NOT NULL COMMENT '신고한 회원 번호', -- FK
	RP_DATE DATETIME DEFAULT (current_time) COMMENT '신고 일자',
	RP_CONTENT VARCHAR(500) NOT NULL COMMENT '신고 내용',
	STATUS INT NOT NULL DEFAULT 1 COMMENT '신고 상태'
) COMMENT '고양이 게시판 - 신고';

ALTER TABLE REPORT AUTO_INCREMENT = 100;


DROP TABLE MEMBER;
DROP TABLE BOARD;
DROP TABLE REPLY;
DROP TABLE BLIKE;
DROP TABLE REPORT;