package cn.life3t.life3t.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Lei on 2015/7/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserComment {
    public int auntId;
    public float star;
    public List<Integer> labelIndexs;
    public String text_comment;
}
