package cn.lcdiao.demo.controller;

import cn.lcdiao.demo.service.WxService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by diao on 2019/3/21
 */

@RestController
@RequestMapping("/weixin")
public class WeixinController {

    Logger log = LoggerFactory.getLogger(getClass());
    private static final String APPID = "wxa3d50968643e3bf4";
    private static final String APPSECRET = "54ebccd91b88cfd5d402b314472c0072";

    @GetMapping("/auth")
    public String auth(@RequestParam("code") String code){
        log.info("进入auth方法.......");
        log.info("code={}",code);

        String url="https://api.weixin.qq.com/sns/oauth2/access_token?"
                +"appid=" + APPID + "&secret=" + APPSECRET + "&code="+ code +"&grant_type=authorization_code";
        RestTemplate restTemplate=new RestTemplate();
        String response=restTemplate.getForObject(url,String.class);
        log.info("response={}",response);

        return response;
    }

    @GetMapping("/getUserInfo")
    public String getUserInfo() throws UnsupportedEncodingException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        //第一步：用户同意授权，获取code
        String code = request.getParameter("code");
        //System.out.println(code);
        //第二步：通过code换取网页授权access_token
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" + APPID +
                "&secret=" + APPSECRET +
                "&code=" + code +
                "&grant_type=authorization_code";
        RestTemplate restTemplate=new RestTemplate();
        String result=restTemplate.getForObject(url,String.class);
        //System.out.println(result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String access_token = jsonObject.getString("access_token");
        String openid = jsonObject.getString("openid");

        /*
        第三步：刷新access_token（如果需要）
        由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，
        refresh_token有效期为30天，当refresh_token失效之后，需要用户重新授权。
         */
//        url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
//                "appid=APPID" +
//                "&grant_type=refresh_token" +
//                "&refresh_token=REFRESH_TOKEN";
//        result=restTemplate.getForObject(url,String.class);
        //第四步：拉取用户信息(需scope为 snsapi_userinfo)
        url = "https://api.weixin.qq.com/sns/userinfo?" +
                "access_token=" + access_token +
                "&openid=" + openid +
                "&lang=zh_CN";
        result = new String(restTemplate.getForObject(url,String.class).getBytes("ISO-8859-1"),"utf-8");

        System.out.println(result);
        return result;
    }

    @GetMapping("/token")
    public String token(){

        String response=WxService.getAccessToken();
        log.info("access_token={}",response);
        return response;
    }

    /**
     * signature	微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     * timestamp	时间戳
     * nonce	随机数
     * echostr	随机字符串=
     */
    @GetMapping("/get")
    public String get(@RequestParam String signature,
                   @RequestParam String timestamp,
                   @RequestParam String nonce,
                   @RequestParam String echostr){
        //System.out.println("signature: " + signature);
        //校验请求
        if (WxService.check(timestamp,nonce,signature)) {
            System.out.println("接入成功");
            //验证通过原样返回echostr
            return echostr;
        } else {
            System.out.println("接入失败");
        }
        return null;
    }

    /**
     * 接收消息和事件推送
     */
    @PostMapping("/get")
    public String get(){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
//        System.out.println("接收消息！");
//        try {
//            ServletInputStream is = request.getInputStream();
//            byte[] b = new byte[1024];
//            int len;
//            StringBuilder sb = new StringBuilder();
//            while ((len = is.read(b)) != -1) {
//                sb.append(new String(b,0,len));
//            }
//            System.out.println(sb.toString());
//            /*
//            文本消息
//            <xml>
//                <ToUserName><![CDATA[gh_082e5a43fe20]]></ToUserName>                    开发者微信号
//                <FromUserName><![CDATA[obVOp1ECliBBHJkr_wdwNGUpF3GU]]></FromUserName>   发送方帐号（一个OpenID）
//                <CreateTime>1553221304</CreateTime>                                     消息创建时间 （整型,单位：秒）
//                <MsgType><![CDATA[text]]></MsgType>                                     消息类型，文本为text
//                <Content><![CDATA[阿萨]]></Content>                                    	文本消息内容
//                <MsgId>22236784195715232</MsgId>                                        消息id，64位整型
//            </xml>
//             */
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            Map<String,String> requestMap = WxService.parseRequest(request.getInputStream());
            System.out.println(requestMap);

            //准备回复的数据包
//            String respXml = "<xml>\n" +
//                    "<ToUserName><![CDATA["+ requestMap.get("FromUserName") + "]]></ToUserName>\n" +
//                    "<FromUserName><![CDATA["+ requestMap.get("ToUserName") + "]]></FromUserName>\n" +
//                    "<CreateTime>" + System.currentTimeMillis()/1000 + "</CreateTime>\n" +
//                    "<MsgType><![CDATA[text]]></MsgType>\n" +
//                    "<Content><![CDATA[你好]]></Content>\n" +
//                    "</xml>";
            String respXml = WxService.getResponse(requestMap);
            System.out.println(respXml);
            return respXml;

        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
