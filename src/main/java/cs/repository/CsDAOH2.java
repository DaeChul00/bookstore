package cs.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cs.model.CsVO;

@Repository
public class CsDAOH2 implements CsDAO {

	@Autowired
	Connection conn;

	@Override
	public int save(CsVO cv) {
		String sql = "insert into cs (username, title, content, category) values (?,?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, cv.getUserName());
			ps.setString(2, cv.getTitle());
			ps.setString(3, cv.getContent());
			ps.setString(4, cv.getCategory());
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<CsVO> findAll() {
		String sql = "SELECT * FROM CS ORDER BY ID DESC";
		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			List<CsVO> list = new ArrayList<>();
			while (rs.next()) {
				CsVO cv = CsVO.builder()
						.id(rs.getInt("id"))
						.userName(rs.getString("username"))
						.title(rs.getString("title"))
						.content(rs.getString("content"))
						.category(rs.getString("category"))
						.status(rs.getString("status"))
						.answer(rs.getString("answer"))
						.adminId(rs.getString("adminId"))
						.createdAt(rs.getTimestamp("createdAt"))
						.answeredAt(rs.getTimestamp("answeredAt"))
						.updatedAt(rs.getTimestamp("updatedAt")) //
						.deleted(rs.getBoolean("deleted"))
						.build();
				list.add(cv);
			}
			return list;
		} catch (SQLException e) {
			System.out.println("리스트 조회 실패(findAll)");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public CsVO findById(int id) {
		String sql = "SELECT * FROM CS WHERE ID = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return CsVO.builder()
							.id(rs.getInt("id"))
							.userName(rs.getString("username"))
							.title(rs.getString("title"))
							.content(rs.getString("content"))
							.category(rs.getString("category"))
							.status(rs.getString("status"))
							.answer(rs.getString("answer"))
							.adminId(rs.getString("adminId"))
							.createdAt(rs.getTimestamp("createdAt"))
							.answeredAt(rs.getTimestamp("answeredAt"))
							.updatedAt(rs.getTimestamp("updatedAt")) 
							.deleted(rs.getBoolean("deleted"))
							.build();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int update(CsVO cv) {
		String sql = "UPDATE CS SET STATUS=?, ANSWER=?, ADMINID=? WHERE ID=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, cv.getStatus());
			ps.setString(2, cv.getAnswer());
			ps.setString(3, cv.getAdminId());
			ps.setInt(4, cv.getId());
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int delete(int id) {
		String sql = "DELETE FROM CS WHERE ID = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
}