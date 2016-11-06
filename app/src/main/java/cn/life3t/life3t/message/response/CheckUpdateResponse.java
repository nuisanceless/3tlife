package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by wuguohao on 15-6-15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckUpdateResponse extends BaseResponse
{
    public boolean HasUpdate;
    public boolean NeedUpdate;
    public String NewVersion;
    public String ReleaseNote;
    public String url;
}
