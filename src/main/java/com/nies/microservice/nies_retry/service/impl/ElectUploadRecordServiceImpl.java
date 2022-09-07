package com.nies.microservice.nies_retry.service.impl;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.nies.microservice.nies_retry.domain.ElectUploadRecord;
import com.nies.microservice.nies_retry.mapper.ElectUploadRecordMapper;
import com.nies.microservice.nies_retry.service.ElectUploadRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ElectUploadRecordServiceImpl extends MppServiceImpl<ElectUploadRecordMapper, ElectUploadRecord> implements ElectUploadRecordService {
}
