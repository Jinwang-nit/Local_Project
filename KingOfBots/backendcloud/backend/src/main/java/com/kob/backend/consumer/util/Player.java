package com.kob.backend.consumer.util;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer botId;
    private String botCode;
    private Integer sx;
    private Integer sy;
    private List<Integer> steps;

    public List<Cell> getCells(){
        List<Cell> res = new ArrayList<>();

        int[] dx={-1,0,1,0},dy={0,1,0,-1};
        int x = sx,y = sy;
        int step=0;
        res.add(new Cell(x,y));
        for (int i=0;i<steps.size();i++){
            x+=dx[steps.get(i)];
            y+=dy[steps.get(i)];
            res.add(new Cell(x,y));
            if (!check_tail_increasing(++step)){
                res.remove(0);
            }
        }
        return res;
    }

    public String getStepsString(){
        StringBuilder res = new StringBuilder();
        for (int d:steps){
            res.append(d);
        }
        return res.toString();
    }

    public boolean check_tail_increasing(int steps){
        if (steps <= 10) return true;
        return steps%3 == 1;
    }
}
