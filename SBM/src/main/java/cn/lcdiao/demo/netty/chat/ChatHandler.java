package cn.lcdiao.demo.netty.chat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by diao on 2019/3/4
 */
@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static Map<Integer,Channel> m = new HashMap<>();

    //广播方法
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {

//        String content = textWebSocketFrame.text();
//        Channel iChannel = channelHandlerContext.channel();
//        for (Channel channel : channelGroup){
//            if (channel != iChannel){
//                channel.writeAndFlush(new TextWebSocketFrame(channelHandlerContext.channel().remoteAddress() + ":" +content));
//            } else {
//                channel.writeAndFlush(new TextWebSocketFrame("我自己：" + content));
//            }
//        }
        String json = textWebSocketFrame.text();
        JSONObject jsonObject = JSON.parseObject(json);
        int id = (int)jsonObject.get("id");
        int toid = (int)jsonObject.get("toid");
        String message = (String)jsonObject.get("message");

        Channel iChannel = channelHandlerContext.channel();
        m.put(id,iChannel);

        Channel toChannel = m.get(toid);
        if(toChannel == null || !toChannel.isOpen()){
            iChannel.writeAndFlush(new TextWebSocketFrame("系统通知：对方未上线!"));
        }else{
            toChannel.writeAndFlush(new TextWebSocketFrame(id+"：" +message));
        }

    }



    //进入
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        for(Channel channel : channelGroup){
            channel.writeAndFlush(new TextWebSocketFrame("系统通知：" +ctx.channel().remoteAddress() + "进入聊天室"));
        }
        channelGroup.add(ctx.channel());
    }


    //退出
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.remove(ctx.channel());
        for(Channel channel : channelGroup){
            channel.writeAndFlush(new TextWebSocketFrame("系统通知：" +ctx.channel().remoteAddress() + "退出聊天室"));
        }
    }
}
