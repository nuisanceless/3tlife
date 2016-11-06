package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lei on 2015/8/26.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetInviteCodeResponse extends BaseResponse {
    public String code;
    public String description;
    public String shareContent;
    public String url;
}
