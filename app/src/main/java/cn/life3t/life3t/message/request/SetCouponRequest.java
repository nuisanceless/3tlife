package cn.life3t.life3t.message.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lei on 2015/8/26.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SetCouponRequest {
    public int couponId;
}

