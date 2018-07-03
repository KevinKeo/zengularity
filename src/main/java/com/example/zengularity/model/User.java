package com.example.zengularity.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String privatekey;

    public User() {
    }

    public User(String username, String password, String privatekey){
        this.username = username;
        this.password = password;
        this.privatekey = privatekey;
    }
    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPrivatekey() {
        return privatekey;
    }
    
}
