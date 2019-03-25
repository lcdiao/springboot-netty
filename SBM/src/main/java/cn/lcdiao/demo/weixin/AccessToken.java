package cn.lcdiao.demo.weixin;

/**
 * Created by diao on 2019/3/22
 */
public class AccessToken {
    private String accessToken;
    private long expireTime;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public AccessToken(String accessToken, String expireIn) {
        this.accessToken = accessToken;
        //计算过期的时间
        expireTime = System.currentTimeMillis()+Integer.parseInt(expireIn)*1000;
    }

    //判断token是否过期
    public boolean isExpired(){
        return System.currentTimeMillis() > expireTime;
    }
}
