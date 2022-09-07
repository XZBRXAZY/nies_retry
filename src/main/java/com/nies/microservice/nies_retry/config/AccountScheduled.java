package com.nies.microservice.nies_retry.config;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nies.microservice.nies_retry.service.RetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;


/**
 * @Author dck
 * @Date 2021/1/19
 * @Version V1.0
 **/
@Slf4j
@Component
public class AccountScheduled extends ConfigurerScheduling {
    @Autowired
    private RetryService retryService;


    @Override
    protected void processTask() {
        log.info("TimeConfig:{}",TimeConfig.account_scan_switch);
        log.info("TimeConfig:{}",TimeConfig.corn);
        if (TimeConfig.account_scan_switch) {
            try {
                retryService.downloadElec();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    protected String getCron() {
        return TimeConfig.corn;
    }
}
