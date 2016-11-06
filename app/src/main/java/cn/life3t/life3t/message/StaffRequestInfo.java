package cn.life3t.life3t.message;

import java.io.Serializable;

/**
 * Created by Lei on 2015/7/6.
 */
public class StaffRequestInfo implements Serializable {
    public String date;
    public int startMin;
    public int endMin;
    public int businessId;
    public double longitude;
    public double latitude;

    private static final long serialVersionUID = 3L;
}
