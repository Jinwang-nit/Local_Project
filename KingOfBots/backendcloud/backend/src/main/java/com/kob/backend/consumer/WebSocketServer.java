package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.util.Game;
import com.kob.backend.consumer.util.Jwt;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    private Session session = null;
    public static ConcurrentHashMap<Integer,WebSocketServer> users = new ConcurrentHashMap<>();

    private User user;

    public static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static RestTemplate restTemplate;
    private static BotMapper botMapper;
    public Game game=null;

    private final String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper ){
        WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate = restTemplate;
    }
    @Autowired
    public void setBotMapper(BotMapper botMapper){
        WebSocketServer.botMapper = botMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        Integer userId = Jwt.getUserId(token);
        System.out.println("成功建立连接");
        this.user = userMapper.selectById(userId);
        if (this.user != null) {
            users.put(userId, this);
        }else {
            this.session.close();
        }
        System.out.println(users);
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("断开连接");
        if (this.user!=null){
            users.remove(this.user.getId());
        }
    }

    public static void startGame(Integer aId,Integer bId,Integer aBotId,Integer bBotId){
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId);
        Bot botB = botMapper.selectById(bBotId);
        Game game = new Game(13,
                14,
                20,
                a.getId(),
                b.getId(),
                botA,
                botB
        );
        game.createMap();
        if (users.get(a.getId()) != null)
            users.get(a.getId()).game = game;
        if (users.get(b.getId()) != null)
            users.get(b.getId()).game = game;

        game.start();

        JSONObject respgame = new JSONObject();
        respgame.put("a_id",game.getPlayA().getId());
        respgame.put("b_id",game.getPlayB().getId());
        respgame.put("a_sx",game.getPlayA().getSx());
        respgame.put("a_sy",game.getPlayA().getSy());
        respgame.put("b_sx",game.getPlayB().getSx());
        respgame.put("b_sy",game.getPlayB().getSy());
        respgame.put("map",game.getG());

        JSONObject respA=new JSONObject();
        respA.put("event","start-matching");
        respA.put("opponent_username",b.getUsername());
        respA.put("opponent_photo",b.getPhoto());
        respA.put("game",respgame);
        if (users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        JSONObject respB=new JSONObject();
        respB.put("event","start-matching");
        respB.put("opponent_username",a.getUsername());
        respB.put("opponent_photo",a.getPhoto());
        respB.put("game",respgame);
        if (users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    private void startMatching(Integer botId){
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        data.add("rating",this.user.getRating().toString());
        data.add("bot_id",botId.toString());
        restTemplate.postForObject(addPlayerUrl,data,String.class);

    }
    private void stopMatching(){
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl,data,String.class);
    }

    private void move(int direction){
        if (game.getPlayA().getId().equals(user.getId())){
            if (game.getPlayA().getBotId().equals(-1))
                game.setNextStepA(direction);
        }else if (game.getPlayB().getId().equals(user.getId())){
            if (game.getPlayB().getBotId().equals(-1))
                game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.out.println("接受信息");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)){
            startMatching(data.getInteger("bot_id"));
        }else if ("stop-matching".equals(event)){
            System.out.println("关闭连接");
            stopMatching();
        } else if ("move".equals(event)) {

            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message){
        // 向Client发送消息
        synchronized (this.session){
            try{
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

