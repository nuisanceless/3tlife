package cn.life3t.life3t.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lei on 2015/7/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Time {
    public int startMin;
    public int endMin;
}
