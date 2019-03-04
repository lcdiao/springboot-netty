package cn.lcdiao.demo.service;

import cn.lcdiao.demo.entity.User;

public interface UserService {
    public User getUserById(int userId);

    boolean addUser(User record);

}
