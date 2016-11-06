package cn.life3t.life3t.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by wuguohao on 15-6-1.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffInfo implements Serializable {
    public int id;
    public String name;
    public int serviceTime;
    public float score;
    public boolean isIdle;
    public boolean isVerified;
    public double latitude;
    public double longitude;
    public int distance;
    public String headUrl;

    private static final long serialVersionUID = 2L;
}
