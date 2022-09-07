package com.nies.microservice.nies_retry.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
@Data
public class TimeConfig {
    /**
     * 定时器
     */

    public static String corn;

    public static Boolean account_scan_switch;
    @Value("${TimeConfig.corn:0 0/10 * 1/1 * ? }")
    public  void setCorn(String corn) {
        TimeConfig.corn = corn;
    }
    @Value("${TimeConfig.account_scan_switch:false }")
    public  void setAccount_scan_switch(Boolean account_scan_switch) {
        TimeConfig.account_scan_switch = account_scan_switch;
    }
}
