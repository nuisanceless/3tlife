package cn.life3t.life3t.message.request;

import java.util.List;

/**
 * Created by Lei on 2015/7/15.
 */
public class CreateSofaCleanOrderRequest {
    public int addressId;
    public String date;
    public int startMin;
    public int endMin;
    public String phone;
    public String contact;
    public int auntCount;
    public int sofaCount;
    public String comment;
    public List<Integer> specifiedAuntList;
}
