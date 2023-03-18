package com.kob.backend.controller.record;


import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.ranklist.GetRankListService;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class GetRecordListController {
    @Autowired
    private GetRecordListService getRecordListService;

    @GetMapping("/record/getlist/")
    public JSONObject getList(@RequestParam MultiValueMap<String,String> data){
        Integer page = Integer.parseInt(Objects.requireNonNull(data.getFirst("page")));
        return getRecordListService.getList(page);
    }
}
