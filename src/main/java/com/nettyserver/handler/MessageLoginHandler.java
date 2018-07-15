package com.nettyserver.handler;

import com.nettyserver.network.NetworkMessageController;
import com.nettyserver.network.NetworkMessageID;
import com.nettyserver.network.NetworkTask;
import com.nettyserver.network.ServerErrorID;
import com.nettyserver.pb.CommonProto;
import com.nettyserver.pb.MessageLoginProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@NetworkMessageController(id = NetworkMessageID.MESSAGE_C2S_LOGIN)
public class MessageLoginHandler implements INetworkMessageHandler {
    static Logger logger = LoggerFactory.getLogger(MessageLoginHandler.class);
    private NetworkMessageID _id = NetworkMessageID.MESSAGE_NONE;
    @Override
    public NetworkMessageID getId() {
        return _id;
    }

    @Override
    public void setId(NetworkMessageID id) {
        _id = id;
    }

    @Override
    public void process(NetworkTask task) {
        try {
            CommonProto.MessageWrapper msgWrapper = task.getMsg();
            MessageLoginProto.LoginRequest req = MessageLoginProto.LoginRequest.parseFrom(msgWrapper.getMsg());
            logger.info("process one client is login, passport=" + req.getPassport());

            UUID uuid = UUID.randomUUID();
            logger.info("token uuid=" + uuid.toString());
            CommonProto.LoginToken token = CommonProto.LoginToken.newBuilder().
                    setToken(uuid.toString()).setOutOfDate("100").build();
            MessageLoginProto.LoginResponse res = MessageLoginProto.LoginResponse.newBuilder().
                    setLoginToken(token.toByteString()).setError(ServerErrorID.SERVER_ERROR_OK.ordinal()).build();

            CommonProto.MessageWrapper newMsg = CommonProto.MessageWrapper.newBuilder()
                    .setId(NetworkMessageID.MESSAGE_S2C_LOGIN.ordinal())
                    .setMsg(res.toByteString())
                    .build();

            task.getChannel().write(newMsg);
            task.getChannel().flush();
        }
        catch (Exception ex) {
            logger.error("process exception=" + ex.toString());
        }
    }

}
