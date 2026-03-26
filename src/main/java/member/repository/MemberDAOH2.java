package member.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import member.model.MemberVO;

@Repository // 이 클래스가 데이터 저장소(DAO)임을 Spring에 알림
public class MemberDAOH2 implements MemberDAO {
    
    @Autowired
    private JdbcTemplate jdbcTemplate; // DB 연결 및 실행을 도와주는 Spring 도구
    @Autowired
    private Connection conn;

    @Override
    public MemberVO login(String id, String pw) {
        // 아이디와 비밀번호가 동시에 일치하는 데이터를 찾음
        String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ? AND PASSWORD = ?";
        try {
            // 결과가 1개일 때 사용하며, VO 객체에 값을 자동으로 채워줌
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(MemberVO.class), id, pw);
        } catch (Exception e) {
            // 일치하는 데이터가 없으면 예외가 발생하므로 null 반환
            return null; 
        }
    }

    @Override
    public void signup(MemberVO vo) {
        // 새로운 회원 정보를 DB에 삽입함 (기본 권한은 USER)
        String sql = "INSERT INTO MEMBER (MEMBER_ID, PASSWORD, NAME, EMAIL, ROLE) VALUES (?, ?, ?, ?, 'USER')";
        jdbcTemplate.update(sql, vo.getMemberId(), vo.getPassword(), vo.getName(), vo.getEmail());
    }
    
    @Override
    public void updateMember(MemberVO vo) {
        String sql = "UPDATE MEMBER SET NAME = ?, EMAIL = ? WHERE MEMBER_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vo.getName());
            ps.setString(2, vo.getEmail());
            ps.setString(3, vo.getMemberId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void deleteMember(String memberId) {
        String sql = "DELETE FROM MEMBER WHERE MEMBER_ID = ?";
        jdbcTemplate.update(sql, memberId);
    }
    
    @Override
    public List<MemberVO> findAll(String sort) {
        List<MemberVO> list = new ArrayList<>();
        
        String orderBy = "REGDATE DESC"; // 기본값
        
        if ("name".equals(sort)) orderBy = "NAME ASC";
        else if ("old".equals(sort)) orderBy = "REGDATE ASC";
        else if ("id".equals(sort)) orderBy = "MEMBER_ID ASC";

        // ORDER BY 뒤에 공백이 있는지 꼭 확인하세요!
        String sql = "SELECT * FROM MEMBER ORDER BY " + orderBy;
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MemberVO member = new MemberVO();
                member.setMemberId(rs.getString("MEMBER_ID"));
                member.setName(rs.getString("NAME"));
                member.setEmail(rs.getString("EMAIL"));
                member.setRole(rs.getString("ROLE"));
                member.setRegdate(rs.getTimestamp("REGDATE"));
                list.add(member);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    @Override
    public void updateRole(String memberId, String role) {
        String sql = "UPDATE MEMBER SET ROLE = ? WHERE MEMBER_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setString(2, memberId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MemberVO findById(String memberId) {
        String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ?";
        try {
            // 아이디로 조회해서 결과가 있으면 VO에 담아 반환, 없으면 예외 발생
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(MemberVO.class), memberId);
        } catch (Exception e) {
            // 조회 결과가 없으면 null 반환
            return null;
        }
    }
}