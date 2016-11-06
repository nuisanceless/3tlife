package cn.life3t.life3t.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Lei on 2015/7/6.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffDetail implements Serializable {
    public int id;
    public String name;
    public int serviceTime;
    public float score;
    public String headUrl;
    public String nativeAddress;
    public String workExperience;
    public boolean isVerified;

    private static final long serialVersionUID = 4L;
}
