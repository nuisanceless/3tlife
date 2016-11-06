package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import cn.life3t.life3t.message.StaffInfo;

/**
 * Created by Lei on 2015/6/29.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffListResponse extends BaseResponse  {
    public List<StaffInfo> auntList;
}
