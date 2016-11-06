package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import cn.life3t.life3t.message.AddressInfo;
import cn.life3t.life3t.message.Order;

/**
 * Created by wuguohao on 15-6-15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderListResponse extends BaseResponse
{
    public List<Order> list;
}
