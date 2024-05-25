package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.dao.Subscribers;
import com.skillbox.cryptobot.repository.SubscribeRepository;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableAsync
@Service
@RequiredArgsConstructor
public class NotifyService {

    private final CryptoCurrencyService service;
    private final SubscribeRepository repository;
    private final AbsSender absSender;

    @Value("${telegram.bot.notify.delay.value}")
    public long notifyMinutes;
    @Async
    @Scheduled(fixedRateString = "${telegram.bot.notify.delay.check}", timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void checkBitcoinPrice() {
            double price = service.getBitcoinPrice();

            List<Subscribers> subscribers = repository.
                findAllByPriceIsGreaterThanEqualAndTimeBeforeOrTimeIsNull(price,
                    LocalDateTime.now().minusMinutes(notifyMinutes).plusSeconds(1));
            if (!subscribers.isEmpty()) {
                subscribers.forEach(sub -> sendMessage(sub, price));
            }
    }

    private void sendMessage(Subscribers sub, double price) {
        sub.setTime(LocalDateTime.now());
        log.info("Send message to userId {}, bitcoin price = {} < subscribe {} USD", sub.getIdUser(),
            TextUtil.toString(price), sub.getPrice());
        SendMessage answer = new SendMessage();
        answer.setChatId(sub.getIdChat());
        answer.setText("Пора покупать, стоимость биткоина " + TextUtil.toString(price) + " USD");
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error send message about buying ", e);
        }
    }
}
