package com.nies.microservice.nies_retry.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nies.microservice.nies_retry.domain.DeviceInfo;
import com.nies.microservice.nies_retry.domain.ElectUploadRecord;
import com.nies.microservice.nies_retry.service.ElectUploadRecordService;
import com.nies.microservice.nies_retry.service.RetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RetryServiceImpl implements RetryService {

    @Autowired
    private ElectUploadRecordService electUploadRecordService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private final String TASK_PREFIX="nies_niescloud:task:hour:";
    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000,multiplier = 1.5))
    public String downloadElec(){

        long now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).toInstant(ZoneOffset.of("+8")).toEpochMilli();//上个整点开始
        String key =TASK_PREFIX+ now;
         String value = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(value)){
            return "本时间点已经获取上1小时内的数据";
        }
        stringRedisTemplate.opsForValue().set(key,key,1, TimeUnit.HOURS);
        long instantStart = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).minusHours(1).toInstant(ZoneOffset.of("+8")).toEpochMilli();//上个整点开始
        long instantEnd  = LocalDateTime.now().withMinute(59).withSecond(59).withNano(59).minusHours(1).toInstant(ZoneOffset.of("+8")).toEpochMilli();//上个整点结束
        HashMap<String, Object> loginParamMap = new HashMap<>();
        loginParamMap.put("userName", "hainan");
        loginParamMap.put("password", "123456");
        String post = HttpUtil.post("https://www.powerseu.com/api/Login/login",loginParamMap);
        JSONObject jsonObject = JSONUtil.parseObj(post);
        String access_token = jsonObject.getStr("obj");

        HashMap<String, String> headParamMap = new HashMap<>();
        headParamMap.put("token", access_token);
        String devList = HttpUtil.createPost("https://www.powerseu.com/api/api-plug/getDeviceList").addHeaders(headParamMap).contentType(ContentType.FORM_URLENCODED.getValue()).execute().body();
        JSONObject jsonObject2 = JSONUtil.parseObj(devList);
        JSONArray jsonArray = jsonObject2.getJSONArray("obj");
        List<DeviceInfo> deviceInfoList = jsonArray.toList(DeviceInfo.class);
        List<ElectUploadRecord> allData = new ArrayList<>();
        deviceInfoList.stream().forEach(x1->{
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("equipId", x1.getEquipId());
            paramMap.put("startTime",instantStart);
            paramMap.put("endTime", instantEnd);
            String body = HttpUtil.createPost("https://www.powerseu.com/api/api-plug/getPlugDataByTime").addHeaders(headParamMap).contentType(ContentType.FORM_URLENCODED.getValue()).form(paramMap).execute().body();
            JSONObject jsonObject3 = JSONUtil.parseObj(body);
            JSONArray jsonArray3 = jsonObject3.getJSONArray("obj");
            List<ElectUploadRecord> electUploadRecordList = jsonArray3.toList(ElectUploadRecord.class);
            electUploadRecordList.stream().forEach(x3->{
                x3.setId(x3.getEquipId()+x3.getEquipUploadTime());
                x3.setEquipAddr(x1.getEquipAddr());
                x3.setEquipType(x1.getEquipType());
                x3.setEquipRegistDate(x1.getEquipRegistDate());
                x3.setEquipArea2(x1.getEquipArea2());
                x3.setEquipArea3(x1.getEquipArea3());
                x3.setEquipName(x1.getEquipName());
            });
            allData.addAll(electUploadRecordList);
        });
        electUploadRecordService.saveOrUpdateBatch(allData);
        return allData==null ? null : String.valueOf(allData.size());
    }

    public static void main(String[] args) {
        long ye = LocalDate.now().minusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long to = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long instantStart = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).minusHours(1).toInstant(ZoneOffset.of("+8")).toEpochMilli();//上个整点开始
        long instantEnd  = LocalDateTime.now().withMinute(59).withSecond(59).withNano(59).minusHours(1).toInstant(ZoneOffset.of("+8")).toEpochMilli();//上个整点结束
        long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        long milli = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).toInstant(ZoneOffset.of("+8")).toEpochMilli();//当前时间的整点
        long now2 = LocalDateTime.now().withMinute(29).withSecond(59).withNano(59).toInstant(ZoneOffset.of("+8")).toEpochMilli();//当前时间的中间点
        LocalDateTime localDateTimeBanDian = LocalDateTime.now().withMinute(29).withSecond(59).withNano(59);
        boolean before = LocalDateTime.now().isBefore(localDateTimeBanDian);
        System.out.println(ye);
        System.out.println(to);
        System.out.println(second);
        System.out.println(milli);
        System.out.println("1661739009000");
        System.out.println(instantStart);
        System.out.println(instantEnd);
        System.out.println(now);
        System.out.println(now2);
        System.out.println(before);



    }



    /**
     * 最终重试失败处理
     * @param e
     * @return
     */
    @Recover
    public String recover(Exception e){

        System.out.println("代码执行重试后依旧失败");
        return "fail";
    }
}
