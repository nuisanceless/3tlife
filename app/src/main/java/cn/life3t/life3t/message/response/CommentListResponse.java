package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import cn.life3t.life3t.message.Comment;

/**
 * Created by wuguohao on 15-6-15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentListResponse extends BaseResponse
{
    public List<Comment> auntCommentList;
    public int totalItem;
}
