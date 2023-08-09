package com.beiyoufamily.web.socket;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yi Dai daiyi.lucky@qq.com
 * @since 2023/8/8 14:40
 */

@Slf4j
@Component
@ServerEndpoint("/peerServerEndpoint/{peerId}")
public class PeerServerEndpoint {

    private static final Map<String, Session> peerIdSessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("peerId") String peerId) {
        log.info("on open:the session is is :{},the peer id is:{}", session.getId(), peerId);
        Session removedSession = PeerServerEndpoint.peerIdSessionMap.remove(peerId);
        try {
            if (Objects.nonNull(removedSession)) {
                removedSession.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            PeerServerEndpoint.peerIdSessionMap.put(peerId, session);
            refreshOnlineSessionsList();
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("peerId") String peerId) {
        log.warn("on close:the session is is :{},the peer id is:{}", session.getId(), peerId);

        Session removedSession = PeerServerEndpoint.peerIdSessionMap.remove(peerId);
        try {
            if (Objects.nonNull(removedSession)) {
                removedSession.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            refreshOnlineSessionsList();
        }
    }

    @OnError
    public void onError(Session session, Throwable e, @PathParam("peerId") String peerId) {
        log.error("on error:the session is is :{},the exception class is: {},the peer id is:{}", session.getId(), e.getClass(), peerId);
        onClose(session, peerId);

        e.printStackTrace();
    }

    private void refreshOnlineSessionsList() {
        PeerServerEndpoint.peerIdSessionMap.forEach((key, value) -> {
            value.getAsyncRemote().sendText(JSON.toJSONString(PeerServerEndpoint.peerIdSessionMap.keySet()));
        });
    }

}
