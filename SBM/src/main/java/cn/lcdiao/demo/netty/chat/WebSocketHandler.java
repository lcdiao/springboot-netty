package cn.lcdiao.demo.netty.chat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by diao on 2019/3/4
 */
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends ChannelInboundHandlerAdapter{

    WebSocketServerHandshaker handshaker;

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static Map<String,Channel> onlineUser = new HashMap<>();

    private String id;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //判断是否websocket握手
        if(msg instanceof FullHttpRequest){
            //不是websocket请求该怎么处理？
            FullHttpRequest request = (FullHttpRequest)msg;

            if(!request.decoderResult().isSuccess() || "websocket".equals(request.headers().get("Upgradle"))){
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);

                if (response.status().code() != 200){
                    ByteBuf byteBuf = Unpooled.copiedBuffer("请求异常", CharsetUtil.UTF_8);
                    response.content().writeBytes(byteBuf);
                    byteBuf.release();
                }
                ctx.writeAndFlush(response);
                return;
            }

            WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory("ws://localhost:8888/websocket",null,false);

            //处理掉请求最后的id参数
            String uri = request.uri();
            //System.out.println(uri);
            String[] as = uri.split("/");
            //System.out.println(as[as.length-1]);
            id = as[as.length-1];
            request.setUri(uri.substring(0,id.length()));
            //System.out.println(request.uri());
            onlineUser.put(id,ctx.channel());

            handshaker = webSocketServerHandshakerFactory.newHandshaker(request);

            if (handshaker == null){
                webSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            }else {
                handshaker.handshake(ctx.channel(),request);
            }
        }else if (msg instanceof WebSocketFrame){
            if (msg instanceof CloseWebSocketFrame) {
                handshaker.close(ctx.channel(),(CloseWebSocketFrame)msg);
            }
            if (msg instanceof TextWebSocketFrame){
                String json = ((TextWebSocketFrame)msg).text();
                JSONObject jsonObject = JSON.parseObject(json);
                String toid = jsonObject.getString("toid");
                String message = (String)jsonObject.get("message");

                Channel iChannel = ctx.channel();

                Channel toChannel = onlineUser.get(toid);
                if(toChannel == null || !toChannel.isOpen()){
                    iChannel.writeAndFlush(new TextWebSocketFrame("系统通知：对方未上线!"));
                }else{
                    toChannel.writeAndFlush(new TextWebSocketFrame(id+"：" +message));
                }
            }
        }


    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        for(Channel channel : channelGroup){
            channel.writeAndFlush(new TextWebSocketFrame("系统通知：" +ctx.channel().remoteAddress() + "进入聊天室"));
        }
        channelGroup.add(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        channelGroup.remove(ctx.channel());
        onlineUser.remove(id);
        for(Channel channel : channelGroup){
            channel.writeAndFlush(new TextWebSocketFrame("系统通知：" +ctx.channel().remoteAddress() + "退出聊天室"));
        }
    }
}
