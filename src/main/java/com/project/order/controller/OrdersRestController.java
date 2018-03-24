package com.project.order.controller;

import com.project.order.model.Order;
import com.project.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrdersRestController {
    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping(value = "orders", method = RequestMethod.GET)
    public List<Order> getAllOrders () {
        return orderRepository.findAll();
    }
}
