package kanglinstudio.assistant.entity;

/**
 * 实体类
 * @author hy511
 *
 */
public class Product {

	private String phonenum;
	private String m_Supplier;
	private String m_City;
	private String m_Province;

	public Product() {
	};

	public Product(String phonenum, String province, String city, String supplier) {
		this.phonenum = phonenum;
		this.m_City = city;
		this.m_Province = province;
		this.m_Supplier = supplier;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
		//在归属地后面加上运营商
		if (phonenum
				.matches("^(130|131|132|145|155|156|185|186).*$")) {
			this.m_Supplier = "联通";
		} else if (phonenum
				.matches("^(133|153|1349|180|181|189).*$")) {
			this.m_Supplier = "电信";
		} else {
			this.m_Supplier = "移动";
		}
	}

	public String getLocation() {
		return m_Province + m_City + "[" + m_Supplier + "]";
	}

	public void setProvince(String province) {
		this.m_Province = province;
	}
	
	public void setCity(String city)
	{
		this.m_City = city;
	}
	
	public void setSupplier(String supplier)
	{
		this.m_Supplier = supplier;
	}

}
