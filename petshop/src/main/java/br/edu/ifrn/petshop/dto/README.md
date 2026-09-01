# dto

Objetos de transferência de dados (Data Transfer Objects) usados na entrada
e saída da API.

Evitam expor as entidades da camada `model` diretamente, permitindo moldar
o formato dos dados trafegados na requisição e na resposta (ex.:
`PetRequestDTO`, `PetResponseDTO`).
