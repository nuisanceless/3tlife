package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by wuguohao on 15-6-12.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoResponse extends BaseResponse implements Serializable {
    public int userId;
    public String phone;
    public String nickname;
    public String email;
    public int gender;

    private static final long serialVersionUID = 0L;
}
