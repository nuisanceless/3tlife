package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import cn.life3t.life3t.message.OrderBusinessDetail;
import cn.life3t.life3t.message.StaffDetail;
import cn.life3t.life3t.message.StaffInfo;

/**
 * Created by wuguohao on 15-6-8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDetailResponse extends BaseResponse {
    public int id;
    public String orderNum;
    public String date;
    public int startMin;
    public String contact;
    public String phone;
    public String comment;
    public String address;
    public int businessId;
    public int endMin;
    public int auntCount;
    public int status;
    public int price;
    public OrderBusinessDetail businessDetail;
    public boolean MemberAlreadyCommented;
    public List<StaffDetail> auntList;
}
