package com.skillbox.cryptobot.repository;

import com.skillbox.cryptobot.dao.Subscribers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscribeRepository extends JpaRepository<Subscribers, UUID> {

    boolean existsByIdUser(double idUser);

    Optional<Subscribers> findByIdUser(double idUser);

    List<Subscribers> findAllByPriceIsGreaterThanEqualAndTimeBeforeOrTimeIsNull(Double price, LocalDateTime time);
}
