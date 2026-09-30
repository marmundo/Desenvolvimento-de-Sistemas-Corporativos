package br.edu.ifrn.taskapi.factory;

public class SmsNotification implements Notification {

    @Override
    public void enviar(String mensagem) {
        System.out.println("[SMS] Enviando: " + mensagem);
    }
}
