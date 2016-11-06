package cn.life3t.life3t.message.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by RexQian on 2014/12/9.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse {
    public int errorCode;
    public String errorMsg;

    public static String getErrorMessage(BaseResponse resp)
    {
        if (resp == null)
        {
            return "网络不给力，请稍候再试";
        }

        return resp.errorMsg;
    }

    public static boolean hasError(BaseResponse resp)
    {
        return resp == null
                || (resp.errorCode != 0);
    }
}
