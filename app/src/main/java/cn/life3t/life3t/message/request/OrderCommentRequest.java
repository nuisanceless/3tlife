package cn.life3t.life3t.message.request;

import java.util.List;

import cn.life3t.life3t.message.UserComment;

/**
 * Created by Lei on 2015/7/16.
 */
public class OrderCommentRequest {
    public int orderId;
    public List<UserComment> comments;
}
