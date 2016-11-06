package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuguohao on 15-8-6.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetActivityListResponse extends BaseResponse {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ActivityListItem
    {
        public long id;
        public String largeImageUrl;
        public String listImageUrl;
        public int action;
        public String url;
    }

    public List<ActivityListItem> list;
}
