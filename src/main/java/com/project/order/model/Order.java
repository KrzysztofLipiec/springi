package com.project.order.model;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "orderSeq")
    @SequenceGenerator(name = "orderSeq", sequenceName = "orderSeq", allocationSize = 1)
    private Long id;
}
