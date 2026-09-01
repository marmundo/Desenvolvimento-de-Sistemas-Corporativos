# controller

Camada responsável por expor os endpoints da aplicação (REST ou MVC).

Recebe as requisições HTTP, valida os dados de entrada e delega o processamento
para a camada `service`. Não deve conter regra de negócio, apenas orquestração
entre a requisição recebida e a resposta devolvida ao cliente.

Classes aqui são anotadas com `@RestController` (APIs) ou `@Controller` (views).
