package cn.lcdiao.demo.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by diao on 2019/3/4
 */
@Configuration
public class ChatServer {

//    @Autowired
//    WebSocketHandler webSocketHandler;

    @Autowired
    ChatHandler chatHandler;

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup(){
        return new NioEventLoopGroup();
    }
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup(){
        return new NioEventLoopGroup();
    }

    @Bean(name = "bootstrap")
    public ServerBootstrap bootstrap(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup(),workerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //解码为http消息
                            socketChannel.pipeline().addLast(new HttpServerCodec());
                            //向客户端发送html5的文件
                            socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                            //将多条http整合为一条
                            socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
                            //
                            socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/websocket"));
                            //简单实现上面的handler(websocket的握手操作)
                            //socketChannel.pipeline().addLast(webSocketHandler);
                            socketChannel.pipeline().addLast(chatHandler);
                        }
                    })
                .option(ChannelOption.SO_BACKLOG,100)
                .childOption(ChannelOption.SO_KEEPALIVE,true);

        return serverBootstrap;
    }
}
