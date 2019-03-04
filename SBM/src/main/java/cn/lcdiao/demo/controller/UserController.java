package cn.lcdiao.demo.controller;

import cn.lcdiao.demo.entity.User;
import cn.lcdiao.demo.service.UserService;
import com.alibaba.fastjson.JSONObject;
import com.sun.tools.internal.ws.processor.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/showUser")
    @ResponseBody
    public User getUserById(int id){
        long l1 = System.currentTimeMillis();
        User user = this.userService.getUserById(id);
        //System.out.println(JSONObject.toJSON(user));
        long l2 = System.currentTimeMillis();
        System.out.println("花费时间:"+(l2-l1)+"毫秒");
        return user;
    }

}