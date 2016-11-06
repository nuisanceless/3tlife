package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lei on 2015/8/26.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetOrderCouponListResponse extends GetCouponListResponse {
    public int selectedCouponId;
}
