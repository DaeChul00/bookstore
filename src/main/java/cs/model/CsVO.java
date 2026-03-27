package cs.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CsVO {
	private int id; //id´Â ÀÚµ¿ Áõ°¡
	private String userName;
	private String title;
	private String  content;
	private String  category;
	private String  status;
	private String answer;
	private String  adminId;
	private Timestamp createdAt;
	private Timestamp answeredAt;
	private Timestamp updatedAt;
	private boolean deleted;

	
}