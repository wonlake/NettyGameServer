package com.nettyserver.netty;

import com.nettyserver.pb.Common;
import io.netty.channel.ChannelHandlerContext;

import java.nio.channels.Channel;

public class NetworkTask {
    public ChannelHandlerContext getChannel() {
        return channel;
    }

    public void setChannel(ChannelHandlerContext channel) {
        this.channel = channel;
    }

    private ChannelHandlerContext channel;

    public Common.MessageWrapper getMsg() {
        return msg;
    }

    public void setMsg(Common.MessageWrapper msg) {
        this.msg = msg;
    }

    private Common.MessageWrapper msg;
}
