/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.data.push.common;

import com.xiaomi.data.push.uds.po.UdsCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public abstract class Send {

    public static void send(Channel channel, UdsCommand command) {
        if (null == channel || !channel.isOpen()) {
            log.warn("channel is close");
            return;
        }
        if (command.getSerializeType() == -1) {
            command.setSerializeType(RcurveConfig.ins().getCodeType());
        }
        try {
            log.debug("begin send:{}",command.getId());
            ByteBuf buf = command.encode();
            ChannelFuture channelFuture = channel.writeAndFlush(buf);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        log.error("send fail:{},", command.getId(), channelFuture.cause());
                    }
                }
            });
        } catch (Throwable ex) {
            log.error("send error:" + ex.getMessage(), ex);
        }
    }

    public static void sendResponse(Channel channel, UdsCommand response) {
        if (null == channel || !channel.isOpen()) {
            log.warn("channel is close");
            return;
        }
        try {
            log.debug("begin send:{}", response.getId());
            ByteBuf buf = response.encode();
            ChannelFuture channelFuture = channel.writeAndFlush(buf);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        log.error("send fail:{},", response.getId(), channelFuture.cause());
                    }
                }
            });
        } catch (Throwable ex) {
            log.error("send response error:" + ex.getMessage(), ex);
        }
    }


    public static void sendMessage(Channel channel, String message) {
        UdsCommand msg = UdsCommand.createRequest();
        msg.setCmd("message");
        msg.setData(message);
        send(channel, msg);
    }

}
