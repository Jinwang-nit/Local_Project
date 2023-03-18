package com.kob.backend.consumer.util;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game  extends Thread{
    private final int rows;
    private final int cols;
    private final int[][] g;
    private final int inner_walls_count;
    private final int dx[] = {-1,0,1,0},dy[] = {0,1,0,-1};
    private final Player playA,playB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;

    private String status = "playing"; // finished
    private String loser = ""; // all:平局
    private final String addBotUrl = "http://127.0.0.1:3002/bot/add/";
    private ReentrantLock lock = new ReentrantLock();
    public Game(int rows, int cols, int inner_walls_count, Integer idA, Integer idB, Bot botA, Bot botB){
        this.rows = rows;
        this.cols = cols;
        this.g = new int[rows][cols];
        this.inner_walls_count = inner_walls_count;
        Integer botIdA = -1;
        Integer botIdB = -1;
        String botCodeA = "",botCodeB = "";
        if (botA != null){
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (botB != null){
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        playA = new Player(idA,botIdA,botCodeA,rows - 2,1,new ArrayList<>());
        playB = new Player(idB,botIdB,botCodeB,1,cols - 2,new ArrayList<>());

    }

    public int[][] getG(){
        return g;
    }

    public void setNextStepA(Integer nextStepA){
        lock.lock();
        try{
            this.nextStepA = nextStepA;
        }finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB){
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        }finally {
            lock.unlock();
        }
    }

    public Player getPlayA(){
        return playA;
    }

    public Player getPlayB(){
        return playB;
    }

    public boolean draw() {
        for (int i=0;i<this.rows;i++)
            for (int j=0;j<this.cols;j++)
                g[i][j]=0;

        for (int r = 0; r < this.rows; r++) {
            g[r][0] = 1;
            g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c++) {
            g[0][c] = 1;
            g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
         for (int i=0;i<this.inner_walls_count/2;i++){
             for (int j=0;j<1000;j++){
                 int r = random.nextInt(this.rows);
                 int c = random.nextInt(this.cols);
                 if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 -c] == 1) continue;
                 if ((r == this.rows - 2 && c == 1) || (r == 1 && c == this.cols - 2)) continue;
                 g[this.rows - 1 - r][this.cols - 1 - c] = g[r][c] = 1;
                 break;
             }
         }

         return check_map(this.rows - 2 ,1,1,this.cols - 2);

    }

    public boolean check_map(int sx,int sy,int tx,int ty){
        if (sx == tx && sy == ty) return true;
        g[sx][sy]=1;
        for (int i=0;i<4;i++){
            int xx = sx + this.dx[i],yy = sy + this.dy[i];
            if (xx >= this.rows || yy >= this.cols || xx < 0 || yy < 0) continue;
            if (g[xx][yy] != 1 && this.check_map(xx,yy,tx,ty)) {
                g[sx][sy] = 0;
                return true;
            }
        }
        return false;
    }


    public void createMap(){
        for (int i=0;i<1000;i++){
            if (draw())
                break;
        }
    }

    private  String getInput(Player player){
        Player me ,you;
        if (playA.getId().equals(player.getId())){
            me = playA;
            you = playB;
        }else{
            me = playB;
            you = playA;
        }
        return getMapString()+"#"+
                me.getSx()+"#"+
                me.getSy()+"#("+
                me.getStepsString()+")#"+
                you.getSx()+"#"+
                you.getSy()+"#("+
                you.getStepsString()+")";
    }

    private void sendBotCode(Player player){
        if (player.getBotId().equals(-1)){
            return;
        }
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",player.getId().toString());
        data.add("bot_code",player.getBotCode());
        data.add("input",getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl,data,String.class);
    }

    private boolean nextStep(){ // 等待玩家输入

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playA);
        sendBotCode(playB);

        for (int i=0;i<50;i++){
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null){
                        playA.getSteps().add(nextStepA);
                        playB.getSteps().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private void sendMove(){ // 传递移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            nextStepA=nextStepB=null;
            sendAllMessage(resp.toJSONString());
        }finally {
            lock.unlock();
        }
    }

    private boolean check_valid(List<Cell> cellsA,List<Cell> cellsB){
        int n = cellsA.size();
        Cell cell = cellsA.get(n-1);
        if (g[cell.getX()][cell.getY()] == 1) return false;
        for (int i=0;i<n-1;i++){
            if (cellsA.get(i).getX() == cell.getX() && cellsA.get((i)).getY() == cell.getY())
                return false;
        }
        for (int i=0;i<n-1;i++){
            if (cellsB.get(i).getX() == cell.getX() && cellsB.get((i)).getY() == cell.getY())
                return false;
        }

        return true;
    }

    private void judge(){ // 判断输入是否合法
        List<Cell> cellsA = playA.getCells();
        List<Cell> cellsB = playB.getCells();
        boolean validA = check_valid(cellsA,cellsB);
        boolean validB = check_valid(cellsB,cellsA);
        if (!validA || !validB){
            status = "finished";
            if (!validA && !validB){
                loser = "all";
            }else if (!validA){
                loser = "A";
            }else if (!validB){
                loser = "B";
            }
        }

    }

    private void sendAllMessage(String message){
        if (WebSocketServer.users.get(playA.getId()) != null)
            WebSocketServer.users.get(playA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playB.getId()) != null)
            WebSocketServer.users.get(playB.getId()).sendMessage(message);

    }
    public void sendResult(){ // 公布结果
        JSONObject resp = new JSONObject();
        resp.put("event","result");
        resp.put("loser",loser);
        saveToDataBase();
        sendAllMessage(resp.toJSONString());
    }

    private String getMapString(){
        StringBuilder res = new StringBuilder();
        for (int i=0;i<this.rows;i++){
            for (int j=0;j<this.cols;j++){
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void updateUserRating(Player player,Integer rating){
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    private void saveToDataBase(){

        Integer ratingA = WebSocketServer.userMapper.selectById(playA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playB.getId()).getRating();
        if (loser.equals("A")){
            ratingB += 5;
            ratingA -= 2;
        }else if ("B".equals(loser)){
            ratingA += 5;
            ratingB -= 2;
        }

        updateUserRating(playA,ratingA);
        updateUserRating(playB,ratingB);
        Record record = new Record(
                null,
                playA.getId(),
                playA.getSx(),
                playA.getSy(),
                playB.getId(),
                playB.getSx(),
                playB.getSy(),
                playA.getStepsString(),
                playB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }

    @Override
    public void run() {
        for (int i=0;i<1000;i++){
            if (nextStep()){ // 是否获取下一步操作
                judge();
                if (status.equals("playing")){
                    sendMove();

                }else {
                    sendResult();
                    break;
                }
            }else{
                status = "finished";
                lock.lock();
                try{
                    if (nextStepA == null && nextStepB == null){
                        loser="all";
                    }else if (nextStepA == null){
                        loser="A";
                    }else {
                        loser="B";
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }

    }
}
