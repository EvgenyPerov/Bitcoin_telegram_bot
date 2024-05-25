package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.dao.Subscribers;
import com.skillbox.cryptobot.repository.SubscribeRepository;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.NoSuchElementException;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SubscribeCommand implements IBotCommand {

    private final CryptoCurrencyService service;
    private final SubscribeRepository repository;
    private static final String SUBSCRIBE_TOPIC = "subscribe";
    @Override
    public String getCommandIdentifier() {
        return SUBSCRIBE_TOPIC;
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    @Transactional
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {

        String newPriceStr = message.getText().substring(SUBSCRIBE_TOPIC.length()+1).trim();
        try {
            Subscribers subscribe = repository.findByIdUser(message.getFrom().getId()).get();
            subscribe.setPrice(Double.valueOf(newPriceStr));

            SendMessage answer = SendMessage.builder()
                .chatId(message.getChatId())
                .text("Текущая цена биткоина " + TextUtil.toString(service.getBitcoinPrice()) + " USD")
                .build();
            absSender.execute(answer);

            answer.setText("Новая подписка создана на стоимость " + newPriceStr + " USD");
            absSender.execute(answer);
        } catch (NumberFormatException e) {
            SendMessage answer = SendMessage.builder()
                .chatId(message.getChatId())
                .text("Для подписки нужно ввести сумму после /subscribe через пробел")
                .build();
            try {
                absSender.execute(answer);
            } catch (TelegramApiException ex) {
                log.error("Ошибка возникла в /subscribe методе", e);
            }
        } catch (NoSuchElementException e){
            log.error("Пользователь не найден", e);
        } catch (TelegramApiException e) {
            log.error("Ошибка возникла в /subscribe методе", e);
        }
    }
}