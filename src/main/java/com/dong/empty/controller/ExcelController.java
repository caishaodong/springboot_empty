package com.dong.empty.controller;

import com.dong.empty.global.ResponseResult;
import com.dong.empty.global.base.BaseController;
import com.dong.empty.global.util.date.LocalDateTimeUtil;
import com.dong.empty.global.util.file.excel.ExcelUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author caishaodong
 * @Date 2020-10-16 10:50
 * @Description
 **/
@RestController
@RequestMapping("/excel")
public class ExcelController extends BaseController {

    @GetMapping("/exportByTemplate")
    public ResponseResult exportByTemplate(HttpServletResponse response) {
        String templatePath = System.getProperty("user.dir") + "/src/main/resources/templates/excel/exportTemplate.xlsx";
        Map<String, String> map = getExpList();
        try {
            ExcelUtil.createSheetByTemplate(templatePath, map, response);
        } catch (Exception e) {
            e.printStackTrace();
            return error();
        }
        return success();
    }

    public Map<String, String> getExpList() {
        Map<String, String> map = new HashMap<>();
        map.put("date", LocalDateTimeUtil.formatDateTimeByPattern(LocalDateTime.now(), "yyyy-MM-dd"));
        map.put("sales", "1234");
        map.put("customservice", "客服张三");
        map.put("customername", "金主爸爸");
        map.put("customertype", "客户类型1");
        // 需求简述
        map.put("demandBrief", "balabala");
        // 合同条款
        map.put("cbmTerms", "巴拉巴拉巴拉");
        //客户要求
        map.put("customerDemand", "这是一个很无理的要求");
        // 财经法务意见
        map.put("financialLegalOpinion", "财经法务表示没意见");
        // 法务意见
        map.put("legalOpinion", "法务表示很无语");
        // 升级处理说明
        map.put("upgradeDealInstruction", "随便升级");
        // 升级处理人（自动显示为特批人）
        map.put("upgradeDealPerson", "李四说我不背锅");
        return map;
    }
}
