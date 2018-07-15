package com.nettyserver.handler;

import com.nettyserver.network.NetworkMessageID;
import com.nettyserver.network.NetworkTask;

public interface INetworkMessageHandler {
    void setId(NetworkMessageID id);
    NetworkMessageID getId();
    void process(NetworkTask task);
}
