package cn.life3t.life3t.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lei on 2015/6/30.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedule {
    public int count;
    public int startMin;
    public int endMin;
}
