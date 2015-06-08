package me.huxos.checkout.entity;

/**
 * 拦截名单实体
 * @author KangLin <kl222@126.com>
 *
 */
public class CBrockerlist {
	private String name;
	private String phone_number;
	private Integer phone_enable;
	private Integer sms_enable;
	
	public CBrockerlist(String phone_number, String name, Integer phone_enable,
			Integer sms_enable) {
		super();
		this.name = name;
		this.phone_number = phone_number;
		this.phone_enable = phone_enable;
		this.sms_enable = sms_enable;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public Integer getPhone_enable() {
		return phone_enable;
	}
	public void setPhone_enable(Integer phone_enable) {
		this.phone_enable = phone_enable;
	}
	public Integer getSms_enable() {
		return sms_enable;
	}
	public void setSms_enable(Integer sms_enable) {
		this.sms_enable = sms_enable;
	}
	
}
