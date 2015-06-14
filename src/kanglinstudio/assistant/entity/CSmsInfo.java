package kanglinstudio.assistant.entity;

/*
 * 短信对象实体类
 * 
 *     _id：短信序号，如100
 * 　　thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的 　　
 * 　　address：发件人地址，即手机号，如+8613811810000
 * 　　person：发件人，如果发件人在通讯录中则为具体姓名，陌生人为null　　
 * 　　date：日期，long型，如1256539465022，可以对日期显示格式进行设置 　　
 * 　　protocol：协议0SMS_RPOTO短信，1MMS_PROTO彩信
 * 　　read：是否阅读0未读，1已读
 * 　　status：短信状态-1接收，0complete,64pending,128failed 　　
 * 　　type：短信类型1是接收到的，2是已发出
 * 　　body：短信具体内容
 * 　　service_center：短信服务中心号码编号，如+8613800755500
 * 
 */
public class CSmsInfo {
	/**
	 * 短信内容
	 */
	private String smsbody;
	/**
	 * 发送短信的电话号码
	 */
	private String phoneNumber;
	/**
	 * 发送短信的日期和时间
	 */
	private long date;
	/**
	 * 发送短信人的姓名
	 */
	private String name;
	/**
	 * 短信类型1是接收到的，2是已发出
	 */
	private int type;

	public String getSmsbody() {
		return smsbody;
	}

	public void setSmsbody(String smsbody) {
		this.smsbody = smsbody;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
