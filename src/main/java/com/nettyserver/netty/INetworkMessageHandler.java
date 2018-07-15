package com.nettyserver.netty;

import com.nettyserver.pb.Common;

public interface INetworkMessageHandler {
    void setId(NetworkMessageID id);
    NetworkMessageID getId();
    void process(NetworkTask task);
}
