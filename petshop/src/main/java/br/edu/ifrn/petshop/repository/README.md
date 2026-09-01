# repository

Camada de acesso a dados.

Interfaces responsáveis por persistir e consultar entidades no banco de
dados, normalmente estendendo `JpaRepository` (ou similar). Não deve conter
regra de negócio, apenas operações de leitura e escrita.

Classes aqui são anotadas com `@Repository` (ou implícito, quando se estende
`JpaRepository`).
