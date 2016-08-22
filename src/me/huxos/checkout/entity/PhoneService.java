package me.huxos.checkout.entity;

/**
 * 电话服务
 * Created by cabe on 16/7/15.
 */
public class PhoneService {
    private String name;
    private String number;
    public PhoneService(String name, String number) {
        setName(name);
        setNumber(number);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
