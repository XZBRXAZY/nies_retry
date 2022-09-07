package com.nies.microservice.nies_retry.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: nies
 * @description: 空调设备信息
 * @author: WXF
 * @create: 2022-06-16 16:16
 */
@Data
public class DeviceInfo {

    private String equipId;
    /**
     * 设备地址
     */
    private String equipAddr;
    /**
     * equipType
     * 1	通用lora插座
     * 1-1	通用lora单相
     * 1-3	通用lora三相
     * 2-2	·通用WiFi插座
     * 2-1	通用WiFi单相
     * 2-3	通用WiFi三相
     */
    private String equipType;
    /**
     * 设备开始使用时间
     */
    private LocalDateTime equipRegistDate;
    /**
     * 设备状态：0离线，1在线
     */
    private Integer equipState;
    /**
     * 设备地址
     */
    private String equipArea2;
    /**
     * 设备所在房间
     */
    private String equipArea3;
    /**
     * 设备名
     */
    private String equipName;

}
