package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lei on 2015/6/9.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse extends BaseResponse {
    public int userId;
    public String phone;
    public String nickName;
    public String email;
    public int gender;
}
