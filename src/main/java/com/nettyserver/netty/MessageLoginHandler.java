package com.nettyserver.netty;

import com.nettyserver.pb.Common;
import com.nettyserver.pb.MsgLoginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            MsgLoginInfo.LoginInfo info = MsgLoginInfo.LoginInfo.parseFrom(task.getMsg().getMsg());
            logger.info("process one client login: passport=" + info.getPassport());

            MsgLoginInfo.LoginToken token = MsgLoginInfo.LoginToken.newBuilder().
                    setToken("this is interesting").setOutOfDate("100").build();
            Common.MessageWrapper newMsg = Common.MessageWrapper.newBuilder()
                    .setId(NetworkMessageID.MESSAGE_S2C_LOGIN.ordinal())
                    .setMsg(token.toByteString())
                    .build();

            task.getChannel().write(newMsg);
            task.getChannel().flush();
        }
        catch (Exception ex) {
            logger.error("process exception=" + ex.toString());
        }
    }

}
