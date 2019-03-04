package cn.lcdiao.demo.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * Created by diao on 2019/3/4
 */
@Component
public class ChatServerApplication {

    @Autowired
    @Qualifier("bootstrap")
    private ServerBootstrap serverBootstrap;

    private Channel channel;

    public void start() throws Exception{
        System.out.println("netty启动");
        channel = serverBootstrap.bind(8888).sync().channel().closeFuture().sync().channel();
    }

    @PreDestroy
    public void close(){
        channel.close();
        channel.parent().close();
    }
}
