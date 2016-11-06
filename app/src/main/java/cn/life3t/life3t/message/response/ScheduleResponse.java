package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import cn.life3t.life3t.message.Schedule;

/**
 * Created by Lei on 2015/6/30.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleResponse extends BaseResponse {
    public List<Schedule> list;
}
