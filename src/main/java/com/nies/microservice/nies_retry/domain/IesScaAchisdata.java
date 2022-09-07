package com.nies.microservice.nies_retry.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 
 *
 * @author lm
 * @date 2022-06-18 15:34:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class IesScaAchisdata extends Model<IesScaAchisdata> {
  private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    @TableField(value = "equipId")
    private String equipId;
    /**
     * 设备开关状态：0关1开
     */
    @TableField(value = "equipState")
    private String equipState;
    /**
     * 数据上传时间
     */
    @TableField(value = "equipUploadTime")
    private Date equipUploadTime;
    /**
     * 耗电量
     */
    @TableField(value = "equipCe")
    private Float equipCe;
    /**
     * 功率
     */
    @TableField(value = "equipRp")
    private Float equipRp;
    /**
     * 频率
     */
    @TableField(value = "equipFr")
    private Float equipFr;
    /**
     * 电流
     */
    @TableField(value = "equipI")
    private Float equipI;
    /**
     * 电压
     */
    @TableField(value = "equipU")
    private Float equipU;

}
