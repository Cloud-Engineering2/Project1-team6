package com.cloud6.place.entity;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;


    @Column(nullable = false, unique = true, length = 100)
    private String username;


    @Column(nullable = false, unique = true, length = 255)
    private String email;


    @Column(nullable = false, length = 255)
    private String password;

    // Review와의 관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

//    // 비밀번호 암호화
//    @PrePersist
//    public void encryptPassword() {
//        if (this.password != null) {
//            this.password = new BCryptPasswordEncoder().encode(this.password);
//            System.out.println("Encrypted Password: " + this.password);
//        }
//    }

    // 생성자 예시 (username, email, password만 받는 생성자)
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}