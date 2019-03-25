package cn.lcdiao.demo.weixin;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Map;

/**
 * Created by diao on 2019/3/22
 */
//设置转换的xml根节点
@XStreamAlias("xml")
public class MusicMessage extends BaseMessage {

    private Music music;

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public MusicMessage(Map<String, String> requestMap, Music music) {
        super(requestMap);
        setMsgType("music");
        this.music = music;
    }


}
