package cn.life3t.life3t.message;

import java.io.Serializable;

/**
 * Created by Lei on 2015/6/8.
 */
public class AddressInfo implements Serializable {
    public int id;
    public String name;
    public String addressProvince;
    public String addressCity;
    public String addressDistrict;
    public String addressStreet;
    public String addressMain;
    public String addressSub;
    public int memberId;
    public int isdefault;
    public String contact;
    public String phone;
    public double longitude;
    public double latitude;

    private static final long serialVersionUID = 1L;
}
