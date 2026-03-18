package com.cms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cms.entity.Contract;
import com.cms.mapper.ContractMapper;
import com.cms.service.IContractService;
import org.springframework.stereotype.Service;

@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements IContractService {
}
