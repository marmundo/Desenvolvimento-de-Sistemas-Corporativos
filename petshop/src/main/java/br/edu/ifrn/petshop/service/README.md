# service

Camada que concentra as regras de negócio da aplicação.

Recebe chamadas da camada `controller`, orquestra os `repository` necessários
e aplica as regras e validações específicas do domínio (ex.: um pet não pode
ter duas consultas no mesmo horário).

Classes aqui são anotadas com `@Service`.
