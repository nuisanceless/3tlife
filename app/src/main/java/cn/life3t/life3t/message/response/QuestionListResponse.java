package cn.life3t.life3t.message.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import cn.life3t.life3t.message.QuestionAnswer;

/**
 * Created by jinlei on 15-8-20.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionListResponse extends BaseResponse
{
    public List<QuestionAnswer> list;
}
