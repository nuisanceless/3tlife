package cn.life3t.life3t.message.type;

import cn.life3t.life3t.R;

/**
 * Created by wuguohao on 15-6-8.
 */
public class OrderType {
    public static final int NORMAL = 1;
    public static final int DEEP = 2;
    public static final int FLOOR = 3;
    public static final int SOFA = 4;
    public static final int HOOD = 5;

    public static int getOrderTypeStringRes(int type)
    {
        switch (type)
        {
            case NORMAL:
                return R.string.cleaning_service_normal;
            case DEEP:
                return R.string.cleaning_service_deep;
            case FLOOR:
                return R.string.cleaning_service_floor;
            case SOFA:
                return R.string.cleaning_service_sofa;
            case HOOD:
                return R.string.cleaning_service_range_hood;
            default:
                return R.string.cleaning_service_normal;
        }
    }

    public static int getOrderTypeImageRes(int type)
    {
        switch (type)
        {
            case NORMAL:
                return R.drawable.cleaning_normal;
            case DEEP:
                return R.drawable.cleaning_deep;
            case SOFA:
                return R.drawable.cleaning_sofa;
            case FLOOR:
                return R.drawable.cleaning_floor;
            case HOOD:
                return R.drawable.cleaning_rangehood;
            default:
                return R.drawable.cleaning_normal;
        }
    }

    public static int getOrderTypeLargeImageRes(int type)
    {
        switch (type)
        {
            case NORMAL:
                return R.drawable.cleaning_normal_large;
            case DEEP:
                return R.drawable.cleaning_deep_large;
            case SOFA:
                return R.drawable.cleaning_sofa_large;
            case FLOOR:
                return R.drawable.cleaning_floor_large;
            case HOOD:
                return R.drawable.cleaning_rangehood_large;
            default:
                return R.drawable.cleaning_normal_large;
        }
    }
}
