package br.edu.ifrn.taskapi.factory;

import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    public Notification criar(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "EMAIL" -> new EmailNotification();
            case "SMS"   -> new SmsNotification();
            case "PUSH"  -> new PushNotification();
            default -> throw new IllegalArgumentException(
                    "Tipo de notificação desconhecido: " + tipo);
        };
    }
}
