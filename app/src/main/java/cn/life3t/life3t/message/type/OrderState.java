package cn.life3t.life3t.message.type;


import cn.life3t.life3t.R;

/**
 * Created by wuguohao on 15-6-3.
 */
public class OrderState {
    /**
     * 新建订单
     */
    public static final int NEW_ORDER = 0;
    /**
     * 客服确认, 已接单
     */
    public static final int ORDER_CONFIRM = 1;
    /**
     * 订单取消
     */
    public static final int ORDER_CANCELED = 2;
    /**
     * 服务开始
     */
    public static final int SERVICE_START = 3;
    /**
     * 服务结束
     */
    public static final int SERVICE_FINISH = 4;
    /**
     * 订单完成
     */
    public static final int ORDER_FINISH = 5;
    /**
     * 在线支付成功
     */
    public static final int ONLINE_PAID = 6;


    public static int getOrderStateStringRes(int stateId) {
        switch (stateId)
        {
            case NEW_ORDER:
                return R.string.order_state_new;
            case ORDER_CONFIRM:
                return R.string.order_state_confirm;
            case ORDER_CANCELED:
                return R.string.order_state_cancel;
            case SERVICE_START:
                return R.string.order_state_service_start;
            case SERVICE_FINISH:
                return R.string.order_state_service_finish;
            case ORDER_FINISH:
                return R.string.order_state_finish;
            case ONLINE_PAID:
                return R.string.order_state_paid;
            default:
                return R.string.order_state_finish;
        }
    }
}
