package cn.lcdiao.demo.exception;

import cn.lcdiao.demo.enums.ResultEnum;

/**
 * Created by diao on 2019/3/21
 */
public class WXException extends RuntimeException {

    private Integer code;

    public WXException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());

        this.code=resultEnum.getCode();
    }
    public WXException(Integer code,String message){
        super(message);
        this.code=code;
    }

}
