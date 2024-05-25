package com.skillbox.cryptobot.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscribers")
public class Subscribers {

    @Id
    @Column
    private UUID id;

    @Column
    private Long idUser;

    @Column
    private Long idChat;

    @Column
    private Double price;

    @Column
    private LocalDateTime time;

}
