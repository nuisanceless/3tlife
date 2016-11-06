package cn.life3t.life3t.message.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lei on 2015/6/1.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest {
    public String phone;
    public String password;
}
