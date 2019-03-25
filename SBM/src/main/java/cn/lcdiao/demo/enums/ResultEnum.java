package cn.lcdiao.demo.enums;


public enum ResultEnum {

    SUCCESS(0,"成功"),

    PARAM_ERROR(1,"参数不正确"),

    WECHAT_MP_ERROR(20,"微信公众账号方面错误"),

    LOGIN_FAIL(25,"登录失败，登录信息不正确"),

    LOGOUT_SUCCESS(26,"登出成功"),
    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
