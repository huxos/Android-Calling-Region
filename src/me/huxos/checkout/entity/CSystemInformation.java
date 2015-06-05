package me.huxos.checkout.entity;

/**
 * 系统信息实体
 * @author  KangLin <kl222@126.com>
 *
 */
public class CSystemInformation {
	private String firewallstatus;
	private String user_name;
	private String user_password;
	public CSystemInformation(String firewallstatus, String user_name,
			String user_password) {
		super();
		this.firewallstatus = firewallstatus;
		this.user_name = user_name;
		this.user_password = user_password;
	}
	public CSystemInformation() {
		// TODO Auto-generated constructor stub
	}
	public String getFirewallstatus() {
		return firewallstatus;
	}
	public void setFirewallstatus(String firewallstatus) {
		this.firewallstatus = firewallstatus;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_password() {
		return user_password;
	}
	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}
}
