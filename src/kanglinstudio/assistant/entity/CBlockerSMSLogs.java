package kanglinstudio.assistant.entity;

public class CBlockerSMSLogs extends CBlockerSMSLog {
	private Integer count;
	private Integer unread_count;
	
	public CBlockerSMSLogs(Integer no, String phone_number, String content,
			long time, Integer isread, Integer count, Integer unread_count) {
		super(no, phone_number, content, time, isread);
		this.count =count;
		this.unread_count = unread_count;
	}

	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getUnread_count() {
		return unread_count;
	}

	public void setUnread_count(Integer unread_count) {
		this.unread_count = unread_count;
	}

}
