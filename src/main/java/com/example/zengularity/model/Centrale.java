package com.example.zengularity.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name= "centrales")
public class Centrale {
    
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
    
    @Column
    @Enumerated(EnumType.STRING)
    private CentraleKind kind;
    
    @Column
    private Double stockage;
    
    @Column 
    private Double stockageMax;
    
    @Column
    private Double consumed;
    
    @Column
    private Double producted;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn
    @JsonIgnore
    private User user;
    
    public Centrale(){
        this.consumed = 0.0;
        this.producted = 0.0;
        this.stockage= 0.0;
    }
    public Centrale(String name, CentraleKind kind, Double stockageMax,User user){
        this.name = name;
        this.kind = kind;
        this.stockageMax = stockageMax;
        this.consumed = 0.0;
        this.producted = 0.0;
        this.stockage= 0.0;
        this.user=user;
    }
    
    public Centrale(Long id,String name, CentraleKind kind, Double stockage,Double stockageMax, Double consumed, Double producted,User user){
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.stockage = stockage;
        this.stockageMax = stockageMax;
        this.consumed = consumed;
        this.producted = producted;
        this.user=user;
    }
    
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CentraleKind getKind() {
        return kind;
    }

    public Double getStockage() {
        return stockage;
    }

    public Double getConsumed() {
        return consumed;
    }

    public Double getProducted() {
        return producted;
    }

    public Double getStockageMax() {
        return stockageMax;
    }

    public User getUser() {
        return user;
    }

}
