package com.nettyserver.netty;

import com.nettyserver.pb.Messages;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by meijun on 2017/5/10.
 */
@Service
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Value("${nettyserver.host:127.0.0.1}")
    String host;

    @Value("${nettyserver.ioThreadNum:5}")
    int ioThreadNum;

    //内核为此套接口排队的最大连接个数，对于给定的监听套接口，内核要维护两个队列，未链接队列和已连接队列大小总和最大值
    @Value("${nettyserver.backlog:1024}")
    int backlog;

    @Value("${nettyserver.port:9090}")
    int port;

    /**
     * 启动
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {
        logger.info("begin to start netty server");
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(ioThreadNum);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, backlog)
                //注意是childOption
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                //.addLast("decoder", new HttpRequestDecoder())
                                //.addLast("encoder", new HttpResponseEncoder())
                                //.addLast(new ServerHandler());
                                .addLast("decoder", new ProtobufVarint32FrameDecoder())
                                .addLast("protoDecoder", new ProtobufDecoder(Messages.MessageWrapper.getDefaultInstance()))
                                .addLast("encoder", new ProtobufVarint32LengthFieldPrepender())
                                .addLast("protoEncoder", new ProtobufEncoder())
                                .addLast("handler", new ProtoBufHandler());
                    }
                });

        channel = serverBootstrap.bind(host,port).sync().channel();

        logger.info("Netty server listening on port "+ port +" and ready for connections...");
    }

    @PreDestroy
    public void stop() {
        logger.info("destroy server resources");
        if (null == channel) {
            logger.error("server channel is null");
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        bossGroup = null;
        workerGroup = null;
        channel = null;
    }
}

