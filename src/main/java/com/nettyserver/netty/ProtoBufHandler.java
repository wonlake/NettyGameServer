package com.nettyserver.netty;

import com.nettyserver.pb.Common;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2017/5/12.
 */
public class ProtoBufHandler extends SimpleChannelInboundHandler<Common.MessageWrapper>
{
    Logger logger = LoggerFactory.getLogger(ProtoBufHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Common.MessageWrapper msgWrapper) throws Exception {
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
