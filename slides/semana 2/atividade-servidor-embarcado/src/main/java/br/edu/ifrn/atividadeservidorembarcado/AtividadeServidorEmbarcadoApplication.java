package br.edu.ifrn.atividadeservidorembarcado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AtividadeServidorEmbarcadoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtividadeServidorEmbarcadoApplication.class, args);
    }

}

@RestController
class StatusController {

    @GetMapping("/")
    public String status() {
        return "Servidor embarcado no ar!";
    }

}
