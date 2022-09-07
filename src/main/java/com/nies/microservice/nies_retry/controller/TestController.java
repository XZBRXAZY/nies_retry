package com.nies.microservice.nies_retry.controller;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nies.microservice.nies_retry.domain.ElectUploadRecord;
import com.nies.microservice.nies_retry.domain.IesScaAchisdata;
import com.nies.microservice.nies_retry.service.ElectUploadRecordService;
import com.nies.microservice.nies_retry.service.RetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ElectUploadRecordService electUploadRecordService;

    //  一定要注入接口，通过接口去调用方法
    @Autowired
    private RetryService retryService;

    @GetMapping("/test")
    void contextLoads() {

        ElectUploadRecord electUploadRecord = new ElectUploadRecord();
        electUploadRecord.setEquipId("1111");
//        electUploadRecord.setEquipUploadTime("1661739009000");
        electUploadRecordService.saveOrUpdateByMultiId(electUploadRecord);
        ElectUploadRecord electUploadRecord2 = new ElectUploadRecord();
        electUploadRecord2.setEquipId("1111");
//        electUploadRecord2.setEquipUploadTime("1661739009000");
        electUploadRecordService.saveOrUpdateByMultiId(electUploadRecord2);
    }



    @GetMapping("/retry")
    public String testRetry() throws Exception {
        return retryService.downloadElec();
    }

    @GetMapping("/tet2")
    public Map<String,Object> tet2(String equipId) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String[] dateList = new String[]{"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00",
                "12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00", "22:00","23:00"};
        List<String> loadT = new ArrayList<>();
        List<String> loadY = new ArrayList<>();
        List<String> loadRateT = new ArrayList<>();
        List<String> loadRateY = new ArrayList<>();
        String startOfToday = String.valueOf(getStartOfDay(today).getTime());
        String endOfToday = String.valueOf(getEndOfDay(today).getTime());
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        Date yesterday = calendar.getTime();
        System.out.println(yesterday);
        String startOfYesterday = String.valueOf(getStartOfDay(yesterday).getTime());
        String endOfYesterday = String.valueOf(getEndOfDay(yesterday).getTime());
        List<IesScaAchisdata> todayList = getList(equipId,startOfToday, endOfToday);
        List<IesScaAchisdata> yesterdayList = getList(equipId, startOfYesterday,endOfYesterday);
        log.info("今天的设备采集信息：【{}】",todayList);
        log.info("昨天的设备采集信息：【{}】",yesterdayList);
        if(yesterdayList!=null && yesterdayList.size()>0){
            for (String dateTime : dateList) {
                AtomicReference<String> load = new AtomicReference<>("0");
                AtomicReference<String> loadRate = new AtomicReference<>("0");
                yesterdayList.forEach(x -> {
                    if(dateTime.equals(getTime(x.getEquipUploadTime(), "HH:mm"))){
                        load.set(String.valueOf(new BigDecimal(x.getEquipRp()).setScale(2,BigDecimal.ROUND_HALF_UP)));
                        log.info("当前计算值：i:{},u:{}",x.getEquipI(),x.getEquipU());
//                        额定功率 2200w   除 100
                        double lodeRate = new BigDecimal(x.getEquipI()).multiply(new BigDecimal(x.getEquipU())).divide(new BigDecimal(22),BigDecimal.ROUND_CEILING).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                        loadRate.set(String.valueOf(lodeRate));
                    }
                });
                loadY.add(load.get());
                loadRateY.add(loadRate.get());
            }
        }
        if(todayList!=null && todayList.size()>0){
            for (String dateTime : dateList) {
                AtomicReference<String> load = new AtomicReference<>("0");
                AtomicReference<String> loadRate = new AtomicReference<>("0");
                todayList.forEach(x -> {
                    if(dateTime.equals(getTime(x.getEquipUploadTime(), "HH:mm"))){
                        load.set(String.valueOf(x.getEquipRp()));
                        log.info("当前计算值：i:{},u:{}",x.getEquipI(),x.getEquipU());
                        double lodeRate = new BigDecimal(x.getEquipI()).multiply(new BigDecimal(x.getEquipU())).divide(new BigDecimal(22),BigDecimal.ROUND_CEILING).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                        loadRate.set(String.valueOf(lodeRate));
                    }
                });
                loadT.add(load.get());
                loadRateT.add(loadRate.get());
            }
        }
        Map<String,Object> map = new HashMap<>();
        int hour = LocalTime.now().getHour();
        map.put("date",dateList);
        if (loadT.size() >= hour + 1) {
            List<String> subLoadT = loadT.subList(0, hour + 1);
            map.put("loadT", subLoadT);
        } else {
            map.put("loadT", loadT);
        }
        map.put("loadY", loadY);
        if (loadT.size() >= hour + 1) {
            List<String> subLoadRateT = loadRateT.subList(0, hour + 1);
            map.put("loadRateT", subLoadRateT);
        } else {
            map.put("loadRateT", loadRateT);
        }
        map.put("loadRateY",loadRateY);
        return map;
    }

    public static Date getStartOfDay(Date date) {
        if (null == date) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }
    public static Date getEndOfDay(Date date) {
        if (null == date) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }


    public static List<IesScaAchisdata> getList(String equipId,String startTime,String endTime){
        HashMap<String, Object> loginParamMap = new HashMap<>();
        loginParamMap.put("userName", "hainan");
        loginParamMap.put("password", "123456");
        String post = HttpUtil.post("https://www.powerseu.com/api/Login/login",loginParamMap);
        JSONObject jsonObject = JSONUtil.parseObj(post);
        String access_token = jsonObject.getStr("obj");


        HashMap<String, String> headParamMap = new HashMap<>();
        headParamMap.put("token", access_token);

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("equipId", equipId);
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
//        String jsonStr = JSONUtil.toJsonStr(paramMap);

        String body = HttpUtil.createPost("https://www.powerseu.com/api/api-plug/getPlugDataByTime").addHeaders(headParamMap).contentType(ContentType.FORM_URLENCODED.getValue()).form(paramMap).execute().body();
        JSONObject jsonObject2 = JSONUtil.parseObj(body);
        JSONArray jsonArray = jsonObject2.getJSONArray("obj");
        List<IesScaAchisdata> deviceInfos = jsonArray.toList(IesScaAchisdata.class);
        return deviceInfos;


    }
    public static String getTime(Date date, String pattern) {
        LocalDateTime localDate = dateToLocalDateTime(date);
        String format = localDate.format(DateTimeFormatter.ofPattern(pattern));
        return format;
    }
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

}
