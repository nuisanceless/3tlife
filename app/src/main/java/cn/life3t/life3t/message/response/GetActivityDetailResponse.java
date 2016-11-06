package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by wuguohao on 15-8-6.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetActivityDetailResponse extends BaseResponse {
    public long id;
    public String detailImageUrl;
    public String title;
    public String introduce; //活动说明
    public String participation; //参与方式
    public String startDate;
    public String endDate;
    public int action;    //执行动作 1: Order 预约
    public long businessId;

    //share
    public String url;
    public String shareContent;
    public String code;

    public static final int ACTION_ORDER = 1;
    public static final int ACTION_INVITE = 2;
}
