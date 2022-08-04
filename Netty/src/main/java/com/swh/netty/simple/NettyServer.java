package com.swh.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyServer {
    public static void main(String[] args) {
        // 创建Boss线程池和work线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // 创建服务器端的启动的对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            /**
             * 1、设置线程组
             * 2、设置线程队列的得到数量
             * 3、设置NioServerSocketChannel为服务器通道的实现
             * 4、设置Work读写处理的Handler
             */
            bootstrap.group(bossGroup,workGroup)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyServerSocketHandler());
                        }
                    });
            // 绑定启动服务端的端口
            ChannelFuture channelFuture=  bootstrap.bind(8080).sync();
            // 等待服务链路断开
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static class NettyServerSocketHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 收到消息的处理
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println(ctx.channel().remoteAddress() + ":" + byteBuf.toString(CharsetUtil.UTF_8));
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            // 消息读取完毕的处理
            ctx.writeAndFlush(Unpooled.copiedBuffer("我们收到你的消息", CharsetUtil.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // 发生异常时的处理
            ctx.close();
        }
    }
}
