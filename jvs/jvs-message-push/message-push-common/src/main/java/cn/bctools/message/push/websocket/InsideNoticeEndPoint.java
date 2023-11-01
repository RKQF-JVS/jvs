package cn.bctools.message.push.websocket;

import cn.bctools.message.push.entity.InsideNotice;
import cn.bctools.message.push.service.impl.InsideNoticeServiceImpl;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint(value = "/inside/notice/{userId}/{tenantId}")
public class InsideNoticeEndPoint {
    public static final String SOCKET_KEY ="%s_%s";

    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, InsideNoticeEndPoint>> webSocketContainer = new ConcurrentHashMap<>();

    private String userId;

    private String sessionId;

    private Session session;

    private String tenantId;

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId,@PathParam("tenantId")String tenantId){
        this.sessionId = session.getId();
        this.userId = userId;
        this.session = session;
        this.tenantId = tenantId;
        String format = String.format(SOCKET_KEY, userId, tenantId);
        if(webSocketContainer.containsKey(format)){
            webSocketContainer.get(format).put(session.getId(),this);
        }else{
            ConcurrentHashMap<String, InsideNoticeEndPoint> websocketMap = new ConcurrentHashMap<>();
            websocketMap.put(session.getId(),this);
            webSocketContainer.put(format,websocketMap);
        }
        this.sendMessageTo(format);
        log.info(userId+":"+this.sessionId+"已连接");
    }

    @OnClose
    public void onClose(Session session){
        String userId = this.userId;
        String sessionId = this.sessionId;
        String tenantId = this.tenantId;
        String socketId = String.format(SOCKET_KEY,userId,tenantId);
        webSocketContainer.get(socketId).remove(sessionId);
        if(webSocketContainer.get(socketId).isEmpty()){
            webSocketContainer.remove(socketId);
        }
        System.out.println(userId+":"+this.sessionId+"已断开连接");
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

    public void sendMessageTo(InsideNotice insideNotice){
        String userId = insideNotice.getUserId();
        String tenantId = insideNotice.getTenantId();
        InsideNoticeServiceImpl insideNoticeService = this.getInsideNoticeService();
        int count = insideNoticeService.count(new LambdaQueryWrapper<InsideNotice>().eq(InsideNotice::getUserId, userId)
                .eq(InsideNotice::getReadIs,Boolean.FALSE));
        Map<String,Object> msgMap = new HashMap<>();
        msgMap.put("remainingCount",count);

        String socketKey = String.format(SOCKET_KEY,userId,tenantId);
        if(webSocketContainer.containsKey(socketKey)){
            if(!userId.equals(insideNotice.getCreateById())){
                ConcurrentHashMap<String, InsideNoticeEndPoint> websocketMap = webSocketContainer.get(socketKey);
                websocketMap.forEach((e,webSocket) -> webSocket.session.getAsyncRemote().sendText(JSONUtil.toJsonStr(msgMap)));
            }

            if(userId.equals(insideNotice.getCreateById())){
                 //修改阅读条件
                insideNotice.setReadIs(Boolean.TRUE);
                //修改阅读状态
                insideNoticeService.updateById(insideNotice);
            }
        }
    }

    public void sendMessageTo(String socketKey){
        InsideNoticeServiceImpl insideNoticeService = this.getInsideNoticeService();
        int count = insideNoticeService.count(new LambdaQueryWrapper<InsideNotice>().eq(InsideNotice::getUserId, this.userId)
                .eq(InsideNotice::getTenantId,this.tenantId)
                .eq(InsideNotice::getReadIs,Boolean.FALSE));
        Map<String,Object> msgMap = new HashMap<>();
        msgMap.put("remainingCount",count);
        if(webSocketContainer.containsKey(socketKey)){
            ConcurrentHashMap<String, InsideNoticeEndPoint> websocketMap = webSocketContainer.get(socketKey);
            websocketMap.forEach(
                    (e,webSocket) -> {
                        if(this.sessionId.equals(webSocket.sessionId)){
                            webSocket.session.getAsyncRemote().sendObject(JSONUtil.toJsonStr(msgMap));
                        }
                    });
        }
    }

    private InsideNoticeServiceImpl getInsideNoticeService(){
        return SpringUtil.getBean("insideNoticeServiceImpl");
    }
}
