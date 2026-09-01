# model

Entidades JPA que representam as tabelas do banco de dados
(ex.: `Pet`, `Cliente`, `Consulta`).

Refletem a estrutura persistida e são usadas pela camada `repository`. Não
devem ser expostas diretamente na API — para isso existe a camada `dto`.

Classes aqui são anotadas com `@Entity`.
