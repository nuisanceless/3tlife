package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by wuguohao on 15-6-9.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffDetailResponse extends BaseResponse
{
    public int id;
    public String name;
    public int serviceTime;
    public float score;
    public String headUrl;
    public String nativeAddress;
    public String workExperience;
    public boolean isVerified;
}
