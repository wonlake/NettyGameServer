package com.nettyserver.netty;

import com.nettyserver.pb.CommonProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/5/12.
 */
public class ProtoBufHandler extends SimpleChannelInboundHandler<CommonProto.MessageWrapper>
{
    Logger logger = LoggerFactory.getLogger(ProtoBufHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, CommonProto.MessageWrapper msgWrapper) throws Exception {
        ClientManager.getInstance().addTask(ctx, msgWrapper);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("exception occurd!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client lost!");
        ClientManager.getInstance().remove(ctx.channel());
        super.channelInactive(ctx);
    }
}
