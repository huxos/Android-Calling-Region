package kanglinstudio.assistant.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统信息实体
 * 
 * @author KangLin <kl222@126.com>
 * 
 */
public class CSystemInformation {
	public Map<String, String> m_Info;

	public CSystemInformation() {
		m_Info = new HashMap<String, String>();
	}

	/**
	 * 防火墙状态
	 * 
	 * @return
	 */
	public String getFirewallstatus() {
		String r = m_Info.get("firewallstatus");
		if (null == r)
			return "0";
		return r;
	}

	public void setFirewallstatus(String firewallstatus) {
		m_Info.put("firewallstatus", firewallstatus);
	}

	/**
	 * 用户名
	 * 
	 * @return
	 */
	public String getUser_name() {
		String s = m_Info.get("user_name");
		if (null == s)
			return "";
		return s;
	}

	public void setUser_name(String user_name) {
		m_Info.put("user_name", user_name);
	}

	/**
	 * 用户密码
	 * 
	 * @return
	 */
	public String getUser_password() {
		String s = m_Info.get("user_password");
		if (null == s)
			return "";
		return s;
	}

	public void setUser_password(String user_password) {
		m_Info.put("user_password", user_password);
	}

	// 拦截方向常量
	public static final int InterceptionDirectionIncoming = 0; // 拦截呼入
	public static final int InterceptionDirectionOutgoing = 1; // 拦截呼出
	public static final int InterceptionDirectionDouble = 2; // 双向拦截

	/**
	 * 拦截方向
	 * 
	 * @return
	 */
	public int getInterceptionDirection() {
		String s = m_Info.get("InterceptionDirection");
		if (null == s)
			return InterceptionDirectionIncoming;
		return Integer.parseInt(s);

	}

	public void setInterceptionDirection(int d) {
		m_Info.put("InterceptionDirection", String.valueOf(d));
	}

	// 拦截方式常量
	public static final int InterceptioinTypeNo = 0; // 不拦截
	public static final int InterceptionTypeNormal = 1; // 正常拦截
	public static final int InterceptioinTypePrompt = 2; // 不提示，不拦截

	public int getInterceptionType() {
		String t = m_Info.get("InterceptionType");
		if (null == t)
			return InterceptionTypeNormal;
		return Integer.parseInt(t);
	}

	public void setInterceptionType(int type) {
		m_Info.put("InterceptionType", String.valueOf(type));
	}

	// 拦截条件常量
	public static final int InterceptionConditionNormal = 0; // 正常拦截（白名单+黑名单）
	public static final int InterceptionConditionNoContact = 1; // 不拦截通信薄中的，其它都拦截

	public int getInterceptionCondition() {
		String c = m_Info.get("InterceptionCondition");
		if (null == c)
			return InterceptionConditionNormal;
		return Integer.parseInt(c);
	}

	public void setInterceptionCondition(int c) {
		m_Info.put("InterceptionCondition", String.valueOf(c));
	}
}
