package kanglinstudio.assistant.entity;

public class CBlockerSMSLog {
	private Integer no;
	private String phone_number;
	private String content;
	private long time;
	private Integer isread;
	
	public CBlockerSMSLog(Integer no, String phone_number, String content, long time,
			Integer isread) {
		super();
		this.no = no;
		this.phone_number = phone_number;
		this.content = content;
		this.time = time;
		this.isread = isread;
	}
	public CBlockerSMSLog(String phone_number, String content, long time,
			Integer isread) {
		super();
		this.phone_number = phone_number;
		this.content = content;
		this.time = time;
		this.isread = isread;
	}

	public Integer getNo() {
		return no;
	}
	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Integer getIsread() {
		return isread;
	}

	public void setIsread(Integer isread) {
		this.isread = isread;
	}
}

