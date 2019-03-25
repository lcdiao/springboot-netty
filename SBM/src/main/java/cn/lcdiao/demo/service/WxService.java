package cn.lcdiao.demo.service;

import cn.lcdiao.demo.weixin.*;
import com.alibaba.fastjson.JSONObject;
import com.thoughtworks.xstream.XStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by diao on 2019/3/21
 */
public class WxService {

    //跟微信开发平台的接口配置信息的Token对应
    private static final String TOKEN = "success";
    private static final String APPID = "wxa3d50968643e3bf4";
    private static final String APPSECRET = "54ebccd91b88cfd5d402b314472c0072";
    //用于存储token
    private static AccessToken at;

    /**
     * 验证签名
     * @param timestamp
     * @param nonce
     * @param signature
     * @return
     */
    public static boolean check(String timestamp, String nonce, String signature) {
        //1）将token、timestamp、nonce三个参数进行字典序排序
        String[] strs = new String[]{TOKEN,timestamp,nonce};
        Arrays.sort(strs);
        //2）将三个参数字符串拼接成一个字符串进行sha1加密
        String str = strs[0]+strs[1]+strs[2];
        //加密
        String mysig = sha1(str);
//        System.out.println(mysig);
//        System.out.println(signature);
        //3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        if (mysig.equals(signature)) {
            return true;
        }

        return false;
    }

    /**
     * 获取用户的基本信息
     * @return
     */
    public static String getUserInfo(String openid) {
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?" +
                "access_token=" + getAccessToken() +
                "&openid=" + openid +
                "&lang=zh_CN";
        RestTemplate restTemplate=new RestTemplate();
        String result=restTemplate.getForObject(url,String.class);
        return result;
    }

    private static void getToken(){
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APPID + "&secret=" + APPSECRET;
        RestTemplate restTemplate=new RestTemplate();
        String response=restTemplate.getForObject(url,String.class);
        JSONObject jsonObject = JSONObject.parseObject(response);
        String token = jsonObject.getString("access_token");
        //过期时间
        String expireIN = jsonObject.getString("expires_in");
        //创建token对象，并存起来
        at = new AccessToken(token,expireIN);
    }

    /**
     * 向外暴露的获取token的方法
     * @return
     */
    public static String getAccessToken(){
        if (at == null || at.isExpired()) {
            getToken();
        }
        return at.getAccessToken();
    }


    /**
     * 进行sha1加密
     * @param str
     * @return
     */
    private static String sha1(String str) {
        try {
            //获取一个加密对象
            MessageDigest md = MessageDigest.getInstance("sha1");
            //加密
            byte[] digest = md.digest(str.getBytes());

            char[] chars = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
            StringBuilder sb = new StringBuilder();

            //处理加密结果
            for (byte b : digest) {
                //byte只有8位，高四位向右移动4位,并只保留后四位(跟00001111进行与操作)
                sb.append(chars[(b >> 4) & 15]);
                //低四位保留
                sb.append(chars[b & 15]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析xml数据包
     * @param is
     * @return
     */
    public static Map<String, String> parseRequest(InputStream is) {
        Map<String,String> map = new HashMap<>();
        SAXReader reader = new SAXReader();
        try {
            //读取输入流，获取文档对象
            Document document = reader.read(is);
            //获取根节点<xml>
            Element root = document.getRootElement();
            //获取根节点的所有子节点
            List<Element> elements = root.elements();
            for (Element e : elements) {
                map.put(e.getName(),e.getStringValue());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;

    }

    /**
     * 用来处理所有的事件和消息的回复
     * @param requestMap
     * @return
     */
    public static String getResponse(Map<String, String> requestMap) {
        BaseMessage msg = null;
        String msgType = requestMap.get("MsgType");
        switch (msgType) {
            //文本
            case "text":
                msg = dealTextMessage(requestMap);
                break;
             //图片
            case "image":

                break;
             //语音
            case "voice":

                break;
             //视频
            case "video":

                break;
             //小视频
            case "shortvideo":

                break;
             //位置
            case "location":

                break;
             //链接
            case "link":

                break;
            default:
                break;
        }
        if (msg != null) {
            //把消息对象处理为xml数据包
            return beanToXml(msg);
        }
        return null;
    }

    /**
     * 把消息对象处理为xml数据包
     * @return
     * @param msg
     */
    private static String beanToXml(BaseMessage msg) {
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

        String xml = stream.toXML(msg);
        return xml;
    }

    /**
     * 处理文本消息
     * @param requestMap
     * @return
     */
    private static BaseMessage dealTextMessage(Map<String, String> requestMap) {

        String msg = requestMap.get("Content");
        if (msg.equals("图文")) {
            List<Article> articles = new ArrayList<>();
            articles.add(new Article("图文标题","图文消息详情","http://mmbiz.qpic.cn/mmbiz_jpg/fp421pGEpLFNGYYuicuYl4gNcHkfSvh8TVAOLNQO716PedkTQNLwDmAc4mI55sdnFB9QwhScOIlZqPGxncribpgA/0","http://www.lcdiao.cn/dist/index.html"));
            NewsMessage nm = new NewsMessage(requestMap,articles);
            return nm;
        }
        if (msg.equals("登录")) {
            String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                    "appid=" + APPID +
                    //用户同意以后跳转的地址
                    "&redirect_uri=" + "http://www.lcdiao.cn/weixin/getUserInfo" +
                    "&response_type=code" +
                    "&scope=snsapi_userinfo" +
                    "#wechat_redirect";
            TextMessage tm = new TextMessage(requestMap,"点击<a href=\""+ url +"\">这里</a>登录");
            return tm;
        }

        TextMessage tm = new TextMessage(requestMap,"你要干啥？");
        return tm;
    }
}
