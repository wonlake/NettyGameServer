package com.nettyserver.netty;

import com.nettyserver.pb.Messages;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/5/12.
 */
public class ProtoBufHandler extends SimpleChannelInboundHandler<Messages.MessageWrapper>
{
    Logger m_logger = LoggerFactory.getLogger(ProtoBufHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Messages.MessageWrapper msgWrapper) throws Exception {
        Messages.Person p = Messages.Person.parseFrom(msgWrapper.getMessage());
        m_logger.info(p.getName());

        Messages.MessageWrapper newMsg = Messages.MessageWrapper.newBuilder()
                .setId(msgWrapper.getId())
                .setMessage(p.toByteString())
                .build();

        ctx.write(newMsg);
        ctx.flush();
    }
}
