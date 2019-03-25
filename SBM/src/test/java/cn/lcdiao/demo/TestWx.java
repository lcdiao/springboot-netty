package cn.lcdiao.demo;

import cn.lcdiao.demo.service.WxService;
import cn.lcdiao.demo.weixin.*;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by diao on 2019/3/22
 */
public class TestWx {

    @Test
    public void testMsg(){
        Map<String,String> requestMap = new HashMap<>();
        requestMap.put("ToUserName","to");
        requestMap.put("FromUserName","from");
        //requestMap.put("MsgType","text");
        TextMessage tm = new TextMessage(requestMap,"还好");

        //将对象转为xml
        XStream stream = new XStream();
        //设置需要处理XStreamAlias("xml")注释的类
        //将根节点换成TextMessage注解上写的xml
        stream.processAnnotations(TextMessage.class);
        stream.processAnnotations(ImageMessage.class);
        stream.processAnnotations(MusicMessage.class);
        stream.processAnnotations(NewsMessage.class);
        stream.processAnnotations(VideoMessage.class);
        stream.processAnnotations(VoiceMessage.class);

        String xml = stream.toXML(tm);

        System.out.println(xml);
    }

    @Test
    public void testGetUserInfo(){
        String result = WxService.getUserInfo("obVOp1ECliBBHJkr_wdwNGUpF3GU");
        System.out.println(result);
    }
}
