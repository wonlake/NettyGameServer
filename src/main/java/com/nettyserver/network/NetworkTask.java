package com.nettyserver.network;

import com.nettyserver.pb.CommonProto;
import io.netty.channel.ChannelHandlerContext;

public class NetworkTask {
    public ChannelHandlerContext getChannel() {
        return channel;
    }

    public void setChannel(ChannelHandlerContext channel) {
        this.channel = channel;
    }

    private ChannelHandlerContext channel;

    public CommonProto.MessageWrapper getMsg() {
        return msg;
    }

    public void setMsg(CommonProto.MessageWrapper msg) {
        this.msg = msg;
    }

    private CommonProto.MessageWrapper msg;
}
