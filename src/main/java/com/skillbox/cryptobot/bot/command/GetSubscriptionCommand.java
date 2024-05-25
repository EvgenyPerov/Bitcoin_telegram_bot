package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.dao.Subscribers;
import com.skillbox.cryptobot.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {

    private final SubscribeRepository repository;
    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        try {
            Subscribers subscribe = repository.findByIdUser(message.getFrom().getId()).get();
            String text = subscribe.getPrice() != null ?
                "Вы подписаны на стоимость биткоина " + subscribe.getPrice() + " USD" :
                "Активные подписки отсутствуют";
            SendMessage answer = SendMessage.builder()
                .chatId(message.getChatId())
                .text(text)
                .build();
            absSender.execute(answer);
        } catch (NoSuchElementException e){
            log.error("Пользователь не найден", e);
        } catch (TelegramApiException e) {
            log.error("Ошибка возникла в /subscribe методе", e);
        }
    }
}