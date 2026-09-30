package br.edu.ifrn.taskapi.factory;

public class EmailNotification implements Notification {

    @Override
    public void enviar(String mensagem) {
        System.out.println("[E-MAIL] Enviando: " + mensagem);
    }
}
