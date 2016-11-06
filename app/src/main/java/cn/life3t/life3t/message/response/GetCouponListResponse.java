package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import cn.life3t.life3t.message.Coupon;

/**
 * Created by Lei on 2015/8/26.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCouponListResponse extends BaseResponse {
    public List<Coupon> list;
}
