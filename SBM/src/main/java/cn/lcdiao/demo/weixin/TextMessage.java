package cn.lcdiao.demo.weixin;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Map;

/**
 * Created by diao on 2019/3/22
 */
//设置转换的xml根节点
@XStreamAlias("xml")
public class TextMessage extends BaseMessage{
    @XStreamAlias("Content")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public TextMessage(Map<String,String> requestMap,String content) {
        super(requestMap);
        //设置文本消息的msgType位text
        this.setMsgType("text");
        this.content = content;
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "toUserName='" + getToUserName() + '\'' +
                ", fromUserName='" + getFromUserName() + '\'' +
                ", createTime='" + getCreateTime() + '\'' +
                ", msgType='" + getMsgType() + '\'' +
                "content='" + content + '\'' +
                '}';
    }
}
