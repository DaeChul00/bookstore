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
		String sql = "INSERT INTO CS (USERNAME, TITLE, CONTENT, CATEGORY) VALUES (?,?,?,?)";
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
						.id(rs.getInt("ID"))
						.userName(rs.getString("USERNAME"))
						.title(rs.getString("TITLE"))
						.content(rs.getString("CONTENT"))
						.category(rs.getString("CATEGORY"))
						.status(rs.getString("STATUS"))
						.answer(rs.getString("ANSWER"))
						.adminId(rs.getString("ADMIN_ID"))
						.createdAt(rs.getTimestamp("CREATED_AT"))
						.answeredAt(rs.getTimestamp("ANSWERED_AT"))
						.updatedAt(rs.getTimestamp("UPDATED_AT"))
						.deleted(rs.getBoolean("IS_DELETED"))
						.build();

				list.add(cv);
			}
			return list;

		} catch (SQLException e) {
			System.out.println("(findAll)");
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
							.id(rs.getInt("ID"))
							.userName(rs.getString("USERNAME"))
							.title(rs.getString("TITLE"))
							.content(rs.getString("CONTENT"))
							.category(rs.getString("CATEGORY"))
							.status(rs.getString("STATUS"))
							.answer(rs.getString("ANSWER"))
							.adminId(rs.getString("ADMIN_ID"))
							.createdAt(rs.getTimestamp("CREATED_AT"))
							.answeredAt(rs.getTimestamp("ANSWERED_AT"))
							.updatedAt(rs.getTimestamp("UPDATED_AT"))
							.deleted(rs.getBoolean("IS_DELETED"))
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
		String sql = "UPDATE CS SET STATUS=?, ANSWER=?, ADMIN_ID=? WHERE ID=?";
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