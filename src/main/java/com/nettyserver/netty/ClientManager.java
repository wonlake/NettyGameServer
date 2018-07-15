package com.nettyserver.netty;

import com.nettyserver.handler.INetworkMessageHandler;
import com.nettyserver.pb.CommonProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import com.nettyserver.network.*;

@Component
public class ClientManager {
    static Logger logger = LoggerFactory.getLogger(ClientManager.class);
    private ConcurrentHashMap<Channel, ChannelHandlerContext> maps = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<NetworkTask> msgQueue = new ConcurrentLinkedQueue<>();

    private ExecutorService threadPoolService;
    private boolean exit = false;
    private HashMap<NetworkMessageID, INetworkMessageHandler> handles = new HashMap<>();

    @Autowired
    private ApplicationContext context;

    private static ClientManager _instance;
    public ClientManager() {
        if(_instance == null)
            _instance = this;
    }
    public ChannelHandlerContext getClient(Channel channel) {
        if(maps.containsKey(channel))
            return maps.get(channel);
        return null;
    }

    public static ClientManager getInstance() {
        return _instance;
    }

    public boolean add(ChannelHandlerContext ctx) {
        Channel id = ctx.channel();
        if(maps.containsKey(id)) {
            logger.error("add channel exist in maps, id=" + id.toString());
            return false;
        }
        return true;
    }

    public boolean remove(Channel id) {
        if(!maps.containsKey(id)) {
            logger.error("remove channel not exist, id=" + id.toString());
            return false;
        }
        return true;
    }

    @PostConstruct
    private void Initialize() {
        initializeNetworkHandlers();
        createMessagePump();
    }

    private void initializeNetworkHandlers() {
        logger.info("createMessagePump");
        Map<String, Object> beans = context.getBeansWithAnnotation(NetworkMessageController.class);
        beans.values().forEach(
                (type) -> {
                    NetworkMessageID id = type.getClass().getAnnotation(NetworkMessageController.class).id();
                    INetworkMessageHandler handler = (INetworkMessageHandler) type;
                    handler.setId(id);
                    handles.put(id, handler);
                    logger.info("add handler id=" + id.toString());
                }
        );
    }

    private void createMessagePump() {
        int numThreads = 10;
        threadPoolService = Executors.newFixedThreadPool(numThreads);
        Runnable specialThread = () -> {
            try {
                while (!exit) {
                    if (msgQueue.isEmpty()) {
                        Thread.sleep(50);
                        continue;
                    }
                    NetworkTask task = msgQueue.poll();
                    int id = task.getMsg().getId();
                    if(id >= NetworkMessageID.MESSAGE_MAX.ordinal() || id <= NetworkMessageID.MESSAGE_NONE.ordinal())
                        continue;

                    NetworkMessageID networkId = NetworkMessageID.values()[id];
                    if(handles.containsKey(networkId)) {
                        Runnable runnable = () -> {
                            handles.get(networkId).process(task);
                        };
                        threadPoolService.execute(runnable);
                    }
                }
            }
            catch (Exception ex) {
                logger.error("specialPoolService exception=" + ex.toString());
            }
        };
        threadPoolService.execute(specialThread);
    }

    public void addTask(ChannelHandlerContext ctx, CommonProto.MessageWrapper msg) {
        NetworkTask task = new NetworkTask();
        task.setChannel(ctx);
        task.setMsg(msg);
        msgQueue.add(task);
    }

    @PreDestroy
    private void DestroyMessagePump() {
        threadPoolService.shutdown();
    }

}
