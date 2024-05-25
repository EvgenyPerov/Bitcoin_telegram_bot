package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.dao.Subscribers;
import com.skillbox.cryptobot.repository.SubscribeRepository;
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
 * Обработка команды отмены подписки на курс валюты
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UnsubscribeCommand implements IBotCommand {

    private final SubscribeRepository repository;

    @Override
    public String getCommandIdentifier() {
        return "unsubscribe";
    }

    @Override
    public String getDescription() {
        return "Отменяет подписку пользователя";
    }

    @Override
    @Transactional
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        try {
            Subscribers subscribe = repository.findByIdUser(message.getFrom().getId()).get();
            subscribe.setPrice(null);

            SendMessage answer = SendMessage.builder()
                .chatId(message.getChatId())
                .text("Подписка отменена")
                .build();
            absSender.execute(answer);

        } catch (NoSuchElementException e){
            log.error("Пользователь не найден", e);
        } catch (TelegramApiException e) {
            log.error("Ошибка возникла в /subscribe методе", e);
        }
    }
}