package cn.life3t.life3t.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lei on 2015/8/25.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coupon {
    public int id;
    public String name;
    public int businessId;
    public int price;
    public boolean isWeekDayLimit;
    public int weekDays;
    public boolean isExpired;
    public int expiredDays;
    public String startDate;
    public String expiredDate;
    public boolean isUsed;
}
