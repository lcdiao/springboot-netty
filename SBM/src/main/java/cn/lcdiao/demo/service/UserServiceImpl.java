package cn.lcdiao.demo.service;

import cn.lcdiao.demo.dao.UserDao;
import cn.lcdiao.demo.entity.User;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;
    Jedis jedis = new Jedis("134.175.116.100",6379);

    @Override
    public User getUserById(int userId) {
//        String json = jedis.get("userid:"+userId);
//        if (json == null || "".equals(json)){
//            User user = userDao.selectByPrimaryKey(userId);   //TODO 还需判断user是否为null...
//            jedis.set("userid:"+userId, JSONObject.toJSONString(user));
//            return user;
//        }else{
//            User user = JSONObject.parseObject(json,User.class);
//            return user;
//        }
        return userDao.selectByPrimaryKey(userId);
    }

    @Override
    public boolean addUser(User record){
        boolean result = false;
        try {
            userDao.insertSelective(record);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
