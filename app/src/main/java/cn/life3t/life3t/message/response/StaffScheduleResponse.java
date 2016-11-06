package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import cn.life3t.life3t.message.Time;

/**
 * Created by Lei on 2015/6/30.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffScheduleResponse extends BaseResponse {
    public List<Time> list;
}
