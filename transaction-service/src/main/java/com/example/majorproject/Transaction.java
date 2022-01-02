package com.example.majorproject;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String transactionId;

    private Double amount;

    @CreationTimestamp
    private Date createdOn;

    private String sender;
    private String receiver;

    @Enumerated(value = EnumType.STRING)
    private TransactionStatus transactionStatus;
}
