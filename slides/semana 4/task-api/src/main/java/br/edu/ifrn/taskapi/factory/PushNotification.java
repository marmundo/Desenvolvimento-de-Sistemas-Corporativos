package br.edu.ifrn.taskapi.factory;

public class PushNotification implements Notification {

    @Override
    public void enviar(String mensagem) {
        System.out.println("[PUSH] Enviando: " + mensagem);
    }
}
