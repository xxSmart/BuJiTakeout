package com.xx.buji.controller;

import com.xx.buji.common.Result;
import com.xx.buji.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping()

public class OrderDetailContorller {
    @Autowired
    private OrderDetailService orderDetailService;

}
