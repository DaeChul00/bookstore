package cs.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import cs.model.CsVO;

@Repository
public class CsDAOH2 implements CsDAO {

    @Autowired
    private DataSource dataSource;

    @Override
    public List<CsVO> findAll() {
        List<CsVO> list = new ArrayList<>();
        String sql = "SELECT * FROM CS ORDER BY ID DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CsVO vo = new CsVO();
                vo.setId(rs.getInt("id"));
                vo.setTitle(rs.getString("title"));

                vo.setUserName(rs.getString("writer")); 

                vo.setCreatedAt(rs.getTimestamp("regdate")); 
                
                list.add(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int save(CsVO cv) {
        String sql = "INSERT INTO CS (CATEGORY, TITLE, CONTENT, WRITER) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cv.getCategory()); 
            ps.setString(2, cv.getTitle());
            ps.setString(3, cv.getContent());
            ps.setString(4, cv.getUserName()); 
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public CsVO findById(int id) {
        String sql = "SELECT * FROM CS WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CsVO vo = new CsVO();
                    vo.setId(rs.getInt("id"));
                    vo.setTitle(rs.getString("title"));
                    vo.setContent(rs.getString("content"));
                    vo.setUserName(rs.getString("writer"));
                    vo.setCreatedAt(rs.getTimestamp("regdate"));
                    
                    return vo;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int update(CsVO cv) {
        String sql = "UPDATE CS SET TITLE = ?, CONTENT = ? WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cv.getTitle());
            ps.setString(2, cv.getContent());
            ps.setInt(3, cv.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int delete(int id) {
        String sql = "DELETE FROM CS WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}