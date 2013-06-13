package me.huxos.checkout.entity;

/**
 * 实体类
 */
public class PhoneArea {

	private Integer _id;
	private String area;

	public PhoneArea(Integer _id, String area) {
		this.area = area;
		this._id = _id;
	}

	public PhoneArea() {
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

}
