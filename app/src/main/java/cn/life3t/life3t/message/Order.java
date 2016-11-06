package cn.life3t.life3t.message;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by wuguohao on 15-5-27.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    public int id;
    public int businessId;
    public String date;
    public int startMin;
    public String address;
    public int status;
    public boolean MemberAlreadyCommented;
    public List<StaffDetail> auntList;
}
