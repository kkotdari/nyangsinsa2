package com.wan.nss.biz.board;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("boardDAO")
public class BoardDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/*
	 * C
	 */
	// 글 작성
	private final String INSERT = "INSERT INTO BOARD (M_NO, B_TITLE, B_CONTENT) VALUES((SELECT M_NO FROM MEMBER WHERE M_ID = ?), ?, ?)";

	// 이미지 insert - TYPE_NO = 201, 202, 203, ...
	private final String INSERT_IMG = "INSERT INTO IMAGE (TARGET_NO, TYPE_NO, I_NM) VALUES((SELECT B_NO FROM BOARD ORDER BY B_NO DESC LIMIT 1), ?, ?)";

	/*
	 * R
	 */
	// 로그인 X : 좋아요 여부 없이 PK 내림차순으로 삭제되지 않은 전체글 보기 (삭제된 글 제외) + 작성자 id도 가져오기
//	private final String SELECT_ALL = "SELECT b.*, m.M_ID FROM BOARD b INNER JOIN MEMBER m ON b.M_NO = m.M_NO WHERE STATUS != 3 ORDER BY B_NO DESC";

	// (사용자의 좋아요 여부 표시된) 삭제되지 않은 게시글 전체 가져오기
	private final String SELECT_ALL = "SELECT b.*, COUNT(LK_NO) AS LIKE_CNT, COUNT(RE_NO) AS REPLY_CNT, IFNULL(i.I_NM, 'default.jpg') AS I_NM, IF(bl.M_NO = (SELECT M_NO FROM MEMBER WHERE M_ID = ?), TRUE, FALSE) AS ISCHECKED "
			+ " FROM (SELECT b.*, m.M_ID FROM BOARD b INNER JOIN MEMBER m ON b.M_NO = m.M_NO WHERE STATUS != 3) b "
			+ " LEFT JOIN (SELECT * FROM REPLY WHERE STATUS != 3) r ON b.B_NO = r.B_NO "
			+ " LEFT JOIN (SELECT * FROM IMAGE WHERE TYPE_NO = 201) i ON b.B_NO = i.TARGET_NO "
			+ " LEFT JOIN (SELECT * FROM BLIKE WHERE M_NO = (SELECT M_NO FROM MEMBER WHERE M_ID = ?)) bl "
			+ " ON b.B_NO = bl.B_NO GROUP BY b.B_NO, i.I_NM, bl.M_NO ORDER BY b.B_NO DESC";

	// TOP3 : 좋아요 많이 받은 글의 작성자의 고양이, 글의 대표 사진 가져오기
	private final String SELECT_ALL_TOP3 = "SELECT m.CAT_NM, b.B_NO, b.B_VIEW, COUNT(LK_NO) AS LIKE_CNT, COUNT(RE_NO) AS REPLY_CNT, IFNULL(i.I_NM, 'default.jpg') AS I_NM "
			+ " FROM BOARD b LEFT JOIN BLIKE bl ON b.B_NO = bl.B_NO LEFT JOIN REPLY r ON b.B_NO = r.B_NO "
			+ " LEFT JOIN MEMBER m ON b.M_NO = m.M_NO "
			+ " LEFT JOIN (SELECT * FROM IMAGE WHERE TYPE_NO = 201) i ON b.B_NO = i.TARGET_NO "
			+ " GROUP BY B_NO, i.I_NM ORDER BY LIKE_CNT DESC, b.B_VIEW DESC, REPLY_CNT DESC LIMIT 3";

	// 게시글 제목 검색
	// ? : 로그인한 사용자 아이디, 검색어, 로그인한 사용자 아이디 (id가 null일 경우 ISCHECKED 는 모두 false
	private final String SELECT_ALL_SEARCH_TITLE = "SELECT b.*, COUNT(LK_NO) AS LIKE_CNT, COUNT(RE_NO) AS REPLY_CNT, IFNULL(i.I_NM, 'default.jpg') AS I_NM, IF(bl.M_NO = (SELECT M_NO FROM MEMBER WHERE M_ID = ?), TRUE, FALSE) AS ISCHECKED "
			+ " FROM (SELECT b.*, m.M_ID FROM BOARD b INNER JOIN MEMBER m ON b.M_NO = m.M_NO WHERE STATUS != 3 AND b.B_TITLE LIKE CONCAT('%', ? ,'%')) b "
			+ " LEFT JOIN (SELECT * FROM BLIKE WHERE M_NO = (SELECT M_NO FROM MEMBER WHERE M_ID = ?)) bl "
			+ " ON b.B_NO = bl.B_NO LEFT JOIN REPLY r ON b.B_NO = r.B_NO "
			+ " LEFT JOIN (SELECT * FROM IMAGE WHERE TYPE_NO = 201) i ON b.B_NO = i.TARGET_NO "
			+ " GROUP BY b.B_NO, i.I_NM, bl.M_NO ORDER BY B_NO DESC";

	// 게시글 내용 검색
	// ? : 로그인한 사용자 아이디, 검색어, 로그인한 사용자 아이디 (id가 null일 경우 ISCHECKED 는 모두 false
	private final String SELECT_ALL_SEARCH_CONTENT = "SELECT b.*, COUNT(LK_NO) AS LIKE_CNT, COUNT(RE_NO) AS REPLY_CNT, IFNULL(i.I_NM, 'default.jpg') AS I_NM, IF(bl.M_NO = (SELECT M_NO FROM MEMBER WHERE M_ID = ?), TRUE, FALSE) AS ISCHECKED "
			+ "FROM (SELECT b.*, m.M_ID FROM BOARD b INNER JOIN MEMBER m ON b.M_NO = m.M_NO WHERE STATUS != 3 AND b.B_CONTENT LIKE CONCAT('%', ? ,'%')) b "
			+ "LEFT JOIN (SELECT * FROM BLIKE WHERE M_NO = (SELECT M_NO FROM MEMBER WHERE M_ID = ?)) bl "
			+ "ON b.B_NO = bl.B_NO LEFT JOIN REPLY r ON b.B_NO = r.B_NO "
			+ "LEFT JOIN (SELECT * FROM IMAGE WHERE TYPE_NO = 201) i ON b.B_NO = i.TARGET_NO "
			+ "GROUP BY b.B_NO, i.I_NM, bl.M_NO ORDER BY B_NO DESC";

	// 게시글 작성자 검색
	// ? : 로그인한 사용자 아이디, 검색어, 로그인한 사용자 아이디 (id가 null일 경우 ISCHECKED 는 모두 false
	private final String SELECT_ALL_SEARCH_WRITER = "SELECT b.*, COUNT(LK_NO) AS LIKE_CNT, COUNT(RE_NO) AS REPLY_CNT, IFNULL(i.I_NM, 'default.jpg') AS I_NM, IF(bl.M_NO = (SELECT M_NO FROM MEMBER WHERE M_ID = ?), TRUE, FALSE) AS ISCHECKED "
			+ "FROM (SELECT b.*, m.M_ID FROM BOARD b INNER JOIN MEMBER m ON b.M_NO = m.M_NO WHERE STATUS != 3 AND m.M_ID LIKE CONCAT('%', ? ,'%')) b "
			+ "LEFT JOIN (SELECT * FROM BLIKE WHERE M_NO = (SELECT M_NO FROM MEMBER WHERE M_ID = ?)) bl "
			+ "ON b.B_NO = bl.B_NO LEFT JOIN REPLY r ON b.B_NO = r.B_NO "
			+ "LEFT JOIN (SELECT * FROM IMAGE WHERE TYPE_NO = 201) i ON b.B_NO = i.TARGET_NO "
			+ "GROUP BY b.B_NO, i.I_NM, bl.M_NO ORDER BY B_NO DESC";

//	----------------- 위까지 완성 ---------------------

	// 로그인 X : 글 상세보기 + 작성자 id
	private final String SELECT_ONE = "SELECT * FROM (SELECT b.*, m.M_ID FROM BOARD b INNER JOIN MEMBER m ON b.M_NO = m.M_NO WHERE STATUS != 3) b "
			+ " LEFT JOIN BLIKE bl ON b.B_NO = bl.B_NO "
			+ " WHERE b.B_NO = ? AND bl.M_NO = (SELECT M_NO FROM MEMBER WHERE M_ID = ?)";
	// 로그인 O : 글 상세보기 + 작성자 id
	private final String SELECT_ONE_BLIKE = "";

	/*
	 * U
	 */
	// 관리자 모드 : 게시글 상태 변경 (0: 정상, 1: 신고, 2: 삭제)
	private final String UPDATE_ADMIN = "UPDATE BOARD SET STATUS = ? WHERE B_NO = ?;";
	// 회원 : 게시글 수정
	private final String UPDATE = "UPDATE BOARD SET B_TITLE = ?, B_CONTENT = ?, B_DATE = CURTIME() WHERE B_NO = ?";
	// 게시글 조회수 +1
	private final String UPDATE_VIEWS = "UPDATE BOARD SET B_VIEW = B_VIEW + 1 WHERE B_NO = ?;";

	/*
	 * D
	 */
	// 회원 : 게시글 삭제
	private final String DELETE = "UPDATE BOARD SET STATUS = 2 WHERE B_NO = ?";

	public boolean insert(BoardVO vo) {
		/* TODO : 조건문 추가하기 */
		jdbcTemplate.update(INSERT, vo.getUserId(), vo.getBoardTitle(), vo.getBoardContent());
		// TODO 이미지 테이블 insert
//		jdbcTemplate.update(null);
		return true;
	}

	public boolean update(BoardVO vo) {
		int res = 0;
		if (vo.getSearchCondition().equals("changeStatus")) {
			// 1. 관리자 모드 : 게시글 상태 변경 STATUS가 있는 경우 status != null
			res = jdbcTemplate.update(UPDATE_ADMIN, vo.getBoardStatus(), vo.getBoardNum());
		} else if (vo.getSearchCondition().equals("edit")) {
			// 2. 회원: 게시글 수정
			/* TODO : 이미지 처리 추가 */
			res = jdbcTemplate.update(UPDATE, vo.getBoardTitle(), vo.getBoardContent(), vo.getBoardNum());
		} else if (vo.getSearchCondition().equals("viewCnt")) {
			// 3. 게시글 조회수 수정
			res = jdbcTemplate.update(UPDATE_VIEWS, vo.getBoardNum());
		}

		if (res < 1) {
			return false;
		}
		return true;
	}

	public boolean delete(BoardVO vo) {
		if (jdbcTemplate.update(DELETE, vo.getBoardNum()) < 1) {
			return false;
		}
		return true;
	}

	public BoardVO selectOne(BoardVO vo) {
		/*
		 * TODO : 좋아요, 이미지 세팅 후 리턴하기
		 */
		return jdbcTemplate.queryForObject(SELECT_ONE, new BoardRowMapper(), vo.getBoardNum());
	}

	public ArrayList<BoardVO> selectAll(BoardVO vo) {
		 /* 주의 : 로그인 했을 경우 BoardVO의 userId에 '현재 로그인한 멤버의 아이디'를 세팅해주세요. */
		
		if (vo.getSearchCondition() == null) { // 1. 글 전체 보기
			// 로그인 안 한 경우 전체 좋아요가 false로 나옴
			return (ArrayList<BoardVO>) jdbcTemplate.query(SELECT_ALL, new BoardRowMapper(), vo.getUserId(), vo.getUserId());
			
		} else if (vo.getSearchCondition().equals("top3")) { // 2. 전체 3등보기
			return (ArrayList<BoardVO>) jdbcTemplate.query(SELECT_ALL_TOP3, new BoardRowMapper());
			
		} else if (vo.getSearchContent() != null) { // 3. 글 검색
			if (vo.getSearchCondition().equals("title")) { // 제목 검색
				return (ArrayList<BoardVO>) jdbcTemplate.query(SELECT_ALL_SEARCH_TITLE, new BoardRowMapper(), vo.getUserId(),vo.getSearchContent(), vo.getUserId());
				
			} else if (vo.getSearchCondition().equals("content")) { // 내용 검색
				return (ArrayList<BoardVO>) jdbcTemplate.query(SELECT_ALL_SEARCH_CONTENT, new BoardRowMapper(), vo.getUserId(), vo.getSearchContent(), vo.getUserId());

			} else if (vo.getSearchCondition().equals("writer")) { // 작성자 검색
				return (ArrayList<BoardVO>) jdbcTemplate.query(SELECT_ALL_SEARCH_WRITER, new BoardRowMapper(), vo.getUserId(), vo.getSearchContent(), vo.getUserId());
			}
		}
		// 아무것도 해당 안 될시 null 반환
		return null;
	}
}

class BoardRowMapper implements RowMapper<BoardVO> {
	@Override
	public BoardVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		BoardVO data = new BoardVO();
		data.setBoardNum(rs.getInt("B_NO"));
		data.setUserNum(rs.getInt("M_NO"));
		data.setBoardTitle(rs.getString("B_TITLE"));
		data.setBoardContent(rs.getString("B_CONTENT"));
		data.setBoardDate(rs.getString("B_DATE"));
		data.setBoardStatus(rs.getInt("STATUS"));
		data.setBoardView(rs.getInt("B_VIEW"));

		// vo에만 존재하는 멤버변수. join 사용해 set
		data.setUserId(rs.getString("M_ID")); // 별칭으로 가져온 작성자 아이디
		data.setLikeCnt(rs.getInt("LIKE_CNT")); // 좋아요 수
		data.setImageName(rs.getString("I_NM")); // 대표 이미지 이름
		data.setChecked(rs.getBoolean("ISCHECKED")); // 좋아요 여부
		data.setReplyCnt(rs.getInt("REPLY_CNT")); // 댓글 수
		// ArrayList<String> imageNames // selectOne 할 때 사진들 이름 넣어줄
		data.setCatName(rs.getString("CAT_NM"));
		return data;
	}
}