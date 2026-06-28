package member.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import member.model.MemberVO;

@Repository // 이 클래스가 데이터 접근(DAO) 객체임을 Spring 관제탑에 알림
public class MemberDAOH2 implements MemberDAO {
	
	// DB 연결 및 실행을 도와주는 Spring 객체 주입 (이것 하나면 락 걱정 없이 완벽합니다!)
	@Autowired
	private JdbcTemplate jdbcTemplate; 

	@Override
	public MemberVO login(String id, String pw) {
		// 아이디와 비밀번호가 동시에 일치하는 데이터를 조회합니다.
		String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ? AND PASSWORD = ?";
		try {
			// 결과가 정확히 1건일 때 VO 객체에 값을 자동으로 바인딩하여 반환합니다.
			return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(MemberVO.class), id, pw);
		} catch (Exception e) {
			// 매칭되는 회원이 없으면 예외가 발생하므로 안전하게 null을 반환합니다.
			return null; 
		}
	}

	@Override
	public void signup(MemberVO vo) {
		// 새로운 회원 정보를 데이터베이스에 삽입합니다. (기본 권한은 USER)
		String sql = "INSERT INTO MEMBER (MEMBER_ID, PASSWORD, NAME, EMAIL, ROLE) VALUES (?, ?, ?, ?, 'USER')";
		jdbcTemplate.update(sql, vo.getMemberId(), vo.getPassword(), vo.getName(), vo.getEmail());
	}
	
	@Override
	public void updateMember(MemberVO vo) {
		// 1. 회원 기본 정보 수정
		String memberSql = "UPDATE MEMBER SET NAME = ?, EMAIL = ? WHERE MEMBER_ID = ?";
		jdbcTemplate.update(memberSql, vo.getName(), vo.getEmail(), vo.getMemberId());
		
		// 2. ⭕ [수정] MemberAddressVO 구조에 맞춰 주소 테이블(MEMBER_ADDRESS) Upsert 처리
		String addressSql = "MERGE INTO MEMBER_ADDRESS (MEMBER_ID, ZIPCODE, ROAD_ADDRESS, ADDR_NAME, IS_DEFAULT) "
		                  + "KEY(MEMBER_ID) VALUES (?, ?, ?, '기본', 'Y')";
		
		jdbcTemplate.update(addressSql, 
			vo.getMemberId(),   
			vo.getZipcode(),    // MemberVO에 담겨온 우편번호
			vo.getRoadAddress() // MemberVO에 담겨온 도로명주소
		);
	}
	
	@Override
	public void deleteMember(String memberId) {
		String sql = "DELETE FROM MEMBER WHERE MEMBER_ID = ?";
		jdbcTemplate.update(sql, memberId);
	}
	
	@Override
	public List<MemberVO> findAll(String sort) {
		String orderBy = "REGDATE DESC"; // 기본 정렬값: 최신 가입 순
		
		if ("name".equals(sort)) orderBy = "NAME ASC";
		else if ("old".equals(sort)) orderBy = "REGDATE ASC";
		else if ("id".equals(sort)) orderBy = "MEMBER_ID ASC";

		String sql = "SELECT * FROM MEMBER ORDER BY " + orderBy;
		
		try {
			// query 메서드와 BeanPropertyRowMapper를 사용하면 while(rs.next()) 루프 없이 바로 리스트 형 변환까지 완료됩니다.
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MemberVO.class));
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>(); // 에러 발생 시 안전하게 빈 리스트 반환
		}
	}
	
	@Override
	public void updateRole(String memberId, String role) {
		String sql = "UPDATE MEMBER SET ROLE = ? WHERE MEMBER_ID = ?";
		jdbcTemplate.update(sql, role, memberId);
	}

	@Override
	public MemberVO findById(String memberId) {
		String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ?";
		try {
			return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(MemberVO.class), memberId);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public MemberVO selectById(String memberId) {
		// 인터페이스 규격을 맞추기 위해 findById와 동일한 로직을 수행하도록 연결합니다.
		return this.findById(memberId);
	}
	
	public MemberVO findByEmail(String email) {
	    String sql = "SELECT * FROM MEMBER WHERE EMAIL = ?";

	    try {
	        return jdbcTemplate.queryForObject(
	            sql,
	            new BeanPropertyRowMapper<>(MemberVO.class),
	            email
	        );
	    } catch (Exception e) {
	        return null;
	    }
	}
	
	
	
}