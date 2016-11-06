package cn.life3t.life3t.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lei on 2015/8/20.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionAnswer {
    public String title;
    public String content;
}
