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
	public CSystemInformation() {
		firewallstatus = "0";
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
