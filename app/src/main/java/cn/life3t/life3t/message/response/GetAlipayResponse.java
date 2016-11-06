package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lei on 2015/7/3.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetAlipayResponse extends BaseResponse {
    public String alipayInfo;
    public int price;
    public int discount;
    public int couponCount;

}
