package cn.lcdiao.demo;

import cn.lcdiao.demo.netty.chat.ChatServer;
import cn.lcdiao.demo.netty.chat.ChatServerApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("cn.lcdiao.demo.dao")
public class DemoApplication {

    public static void main(String[] args) throws Exception {
        //SpringApplication.run(DemoApplication.class, args);


        ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class,args);

        ChatServerApplication chatServerApplication = context.getBean(ChatServerApplication.class);
        chatServerApplication.start();
    }

}

