package com.nies.microservice.nies_retry.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @program: nies
 * @description:
 * @author: WXF
 * @create: 2022-08-30 17:59
 */
@Data
public class MoreDataDto {

    private String equipId;
    /**
     * 设备开关状态：0关1开
     */
    private String equipState;
    /**
     * 数据上传时间
     */
    @JsonFormat(timezone = "GMT")
    private Date equipUploadTime;
    /**
     * 耗电量
     */
    private Float equipCe;
    /**
     * 功率
     */
    private Float equipRp;
    /**
     * 频率
     */
    private Float equipFr;
    /**
     * 电流
     */
    private Float equipI;
    /**
     * 电压
     */
    private Float equipU;

}
