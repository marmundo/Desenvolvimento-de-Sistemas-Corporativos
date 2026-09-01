package br.edu.ifrn.petshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PetshopApplication {

	public static void main(String[] args) {
		var ctx= SpringApplication.run(PetshopApplication.class, args);
		Classe classe = ctx.getBean(Classe.class);
		classe.ola();
	}

	// Esse método cria um bean de Classe para o Spring gerenciar.
	// Quando a aplicação sobe, o Spring Boot registra esse bean no ApplicationContext,
	// então a classe pode ser obtida com ctx.getBean(Classe.class) e usada no main.
	// @Bean
	// public Classe runClasse() {
	// 	return new Classe();
	// }

}
