package com.nies.microservice.nies_retry.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("elect_upload_record")
public class ElectUploadRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.INPUT)
    private String id;
//    @MppMultiId // 复合主键
    private String equipId;

//    @MppMultiId // 复合主键
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime equipUploadTime;



    private String equipI;

    private String equipRp;

    private String equipFr;

    private String equipCe;

    private String equipState;

    private String equipU;
    /****************************************************/
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime equipRegistDate;

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
