package cn.life3t.life3t.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lei on 2015/7/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBusinessDetail {
    public int serviceMin;
    public int area;
    public int sofaCount;
    public int hoodsCount;
}
