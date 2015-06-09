package me.huxos.checkout.entity;

public class CBlockerSmsKeyword {
	Integer no;
	String keyword;
	String phone_number;
	Integer enable;
	
	public CBlockerSmsKeyword(Integer no, String keyword, String phone_number, Integer enable) {
		super();
		this.no = no;
		this.keyword = keyword;
		this.phone_number = phone_number;
		this.enable = enable;
	}
	public CBlockerSmsKeyword(String keyword, String phone_number) {
		super();
		this.keyword = keyword;
		this.phone_number = phone_number;
		this.enable = 1;
	}
	public Integer getNo() {
		return no;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getPhone_number() {
		if(null == phone_number)
			return "";
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public Integer getEnable() {
		return enable;
	}
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
	public void setNo(Integer no) {
		this.no = no;
	}
}
