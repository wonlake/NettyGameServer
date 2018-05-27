package com.nettyserver.netty;

import com.nettyserver.pb.Common;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/5/12.
 */
public class ProtoBufHandler extends SimpleChannelInboundHandler<Common.MessageWrapper>
{
    Logger m_logger = LoggerFactory.getLogger(ProtoBufHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Common.MessageWrapper msgWrapper) throws Exception {
        Common.Person p = Common.Person.parseFrom(msgWrapper.getMsg());
        m_logger.info(p.getName());

        Common.MessageWrapper newMsg = Common.MessageWrapper.newBuilder()
                .setId(msgWrapper.getId())
                .setMsg(p.toByteString())
                .build();

        ctx.write(newMsg);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
    }
}
