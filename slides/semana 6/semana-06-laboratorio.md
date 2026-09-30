# Laboratório Prático — Semana 6
## Central de Tarefas com memória permanente: Spring Data JPA e Hibernate

**Disciplina:** Desenvolvimento de Sistemas Corporativos
**Curso:** Tecnologia em Sistemas para Internet
**Unidade:** 3.4 (continuação) — Mecanismos de persistência
**Formato:** equipes de 3 estudantes | **Duração:** 4 aulas (2 aulas na quarta e 2 na sexta)
**Stack:** Java 17+ (ou superior), Spring Boot 4, Spring Data JPA, H2

---

## 1. Contexto da missão

Até a semana 4, a Central de Tarefas guardava tudo em uma `List` em memória. Basta reiniciar a aplicação para que todo o trabalho desapareça, como um quadro branco apagado ao fim do expediente. Nesta semana, sua equipe fará a "migração para o arquivo da empresa": as tarefas e os usuários passarão a ser persistidos em um banco de dados relacional por meio do **Spring Data JPA** com **Hibernate**.

O objetivo pedagógico é observar, no console, o trabalho que o ORM realiza. Assim como usamos `System.out.println` para acompanhar Controller, Service e Repository, agora usaremos o **SQL impresso pelo Hibernate** como instrumento de observação.

## 2. Objetivos de aprendizagem

Ao final do laboratório, você será capaz de:

1. Mapear classes Java em tabelas com `@Entity`, `@Id`, `@GeneratedValue`, `@Column` e `@Enumerated`.
2. Substituir um repositório em memória por uma interface `JpaRepository`.
3. Criar consultas por **nome de método** (consultas derivadas) e por **`@Query`** (JPQL).
4. Relacionar `User` e `Task` com `@OneToMany` e `@ManyToOne`.
5. Identificar e resolver problemas típicos: loop de serialização e problema N+1.

## 3. Pré-requisitos

- Projeto da Central de Tarefas com as camadas Controller, Service e Repository (semanas 3 e 4), incluindo DTOs em `record`.
- CRUD de `User` em memória (avaliação prática da semana 5), **ainda sem relacionamento** com `Task`.
- IDE configurada e JDK instalado.

> **Adaptação:** os nomes de classes e pacotes deste roteiro (`Task`, `TaskService`, `TaskRepository`, `Priority` etc.) seguem o projeto das semanas anteriores. Se o seu projeto usa nomes diferentes, adapte mantendo a lógica.

## 4. Como o laboratório é pontuado

| Nível | Missão | Etapas | Pontos |
|---|---|---|---|
| Bronze | Persistência básica | 1 a 3 | 10 |
| Prata | Consultas derivadas e `@Query` | 4 e 5 | 15 |
| Ouro | Relacionamento `User` 1:N `Task` | 6 | 20 |
| Diamante | `join fetch`, paginação e DTOs | 7 | 25 |

**Regra de validação:** para cada nível, a equipe demonstra ao professor o funcionamento **e** explica o SQL que apareceu no console.

---

## Etapa 0 — Preparar as dependências

Se o projeto foi gerado com a combinação recomendada (Spring Web, Spring Data JPA, Validation, Lombok), falta apenas o banco. Adicione ao `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

> **Recomendação:** ao criar um projeto novo, gere-o em **start.spring.io** selecionando *Spring Data JPA* e *H2 Database*, o que evita conflitos de versão.

Configure o `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:tarefasdb
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Verificação:** a aplicação inicia sem erros e o console mostra a conexão com o banco `tarefasdb`.

---

## Etapa 1 — Transformar `Task` em entidade (Bronze)

Ajuste a classe do modelo. Note que os enums passam a ser gravados como texto.

```java
package com.example.tarefas.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    private LocalDate dueDate;

    protected Task() { }   // exigido pelo JPA

    public Task(String title, String description, Priority priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    // getters e setters (ou @Getter e @Setter do Lombok)
}
```

Crie os enums, caso ainda não existam:

```java
public enum Priority { LOW, MEDIUM, HIGH }

public enum TaskStatus { PENDING, IN_PROGRESS, DONE }
```

**Pontos de atenção**

- O pacote é **`jakarta.persistence`**, e não `javax.persistence`.
- Use `EnumType.STRING`. O padrão (`ORDINAL`) grava a posição do valor e corrompe dados se o `enum` for reordenado.
- Se usar Lombok, evite `@Data` em entidades. Prefira `@Getter`, `@Setter` e `@NoArgsConstructor(access = AccessLevel.PROTECTED)`.
- Não é possível usar `record` como entidade. Records continuam sendo a escolha para DTOs.

---

## Etapa 2 — Trocar o repositório em memória por `JpaRepository` (Bronze)

Substitua a classe `TaskRepository` (que manipulava uma `List`) por uma interface:

```java
package com.example.tarefas.repository;

import com.example.tarefas.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
```

Adapte o `TaskService`. Mantenha os `System.out.println` das semanas anteriores para acompanhar o fluxo.

```java
@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task criar(Task task) {
        System.out.println("[Service] Criando tarefa: " + task.getTitle());
        return repository.save(task);
    }

    public List<Task> listar() {
        System.out.println("[Service] Listando todas as tarefas");
        return repository.findAll();
    }

    public Task buscarPorId(Long id) {
        System.out.println("[Service] Buscando tarefa id=" + id);
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tarefa não encontrada: " + id));
    }

    public void remover(Long id) {
        System.out.println("[Service] Removendo tarefa id=" + id);
        repository.deleteById(id);
    }
}
```

> Se o `TaskService` das semanas anteriores dependia de métodos como `repository.gerarId()` ou de laços de busca, remova-os: o banco agora gera o id e o repositório já oferece a busca.

---

## Etapa 3 — Observar o ORM em ação (Bronze)

Suba a aplicação e execute, com Postman ou Insomnia:

1. `POST /tasks` com duas ou três tarefas.
2. `GET /tasks`.
3. `GET /tasks/{id}`.
4. `DELETE /tasks/{id}`.

Preencha o quadro abaixo com o SQL observado no console:

| Requisição | SQL impresso pelo Hibernate |
|---|---|
| `POST /tasks` | |
| `GET /tasks` | |
| `GET /tasks/{id}` | |
| `DELETE /tasks/{id}` | |

**Perguntas de reflexão (responder no relatório da equipe)**

1. Onde está o código SQL nas suas classes? Quem o escreveu?
2. O que acontece com os dados ao reiniciar a aplicação? Por que isso ocorre com `create-drop` e H2 em memória?
3. O `TaskController` precisou mudar? E o `TaskService`? O que isso revela sobre a arquitetura em camadas?

> **Checkpoint Bronze:** CRUD de `Task` funcionando com persistência JPA e quadro preenchido.

---

## Etapa 4 — Consultas derivadas (Prata)

Antes de programar, insira dados variados. Uma sugestão de carga inicial (classe que roda na partida):

```java
@Component
public class CargaInicial implements CommandLineRunner {

    private final TaskRepository repository;

    public CargaInicial(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        repository.save(new Task("Estudar JPA", "Ler capítulo de persistência", Priority.HIGH, LocalDate.now().plusDays(2)));
        repository.save(new Task("Revisar DTOs", "Revisão da semana 4", Priority.MEDIUM, LocalDate.now().minusDays(3)));
        repository.save(new Task("Configurar H2", "Ajustar propriedades", Priority.LOW, LocalDate.now().minusDays(1)));
        repository.save(new Task("Escrever testes", "Testes do Service", Priority.HIGH, LocalDate.now().plusDays(7)));
        repository.save(new Task("Preparar apresentação", "Slides da equipe", Priority.MEDIUM, LocalDate.now().plusDays(4)));
        repository.save(new Task("Estudar Spring Security", "Leitura antecipada", Priority.LOW, null));
    }
}
```

Adicione ao `TaskRepository`:

```java
List<Task> findByStatus(TaskStatus status);

List<Task> findByPriorityAndStatus(Priority priority, TaskStatus status);

List<Task> findByTitleContainingIgnoreCase(String trecho);

List<Task> findByStatusOrderByDueDateAsc(TaskStatus status);

long countByStatus(TaskStatus status);

boolean existsByTitleIgnoreCase(String title);
```

Exponha pelo menos quatro deles em endpoints, por exemplo:

| Endpoint | Método do repositório |
|---|---|
| `GET /tasks?status=PENDING` | `findByStatus` |
| `GET /tasks/busca?titulo=estudar` | `findByTitleContainingIgnoreCase` |
| `GET /tasks/contagem?status=DONE` | `countByStatus` |
| `GET /tasks/pendentes-ordenadas` | `findByStatusOrderByDueDateAsc` |

**Experimento de erro controlado:** renomeie temporariamente `findByStatus` para `findByStatuss` e inicie a aplicação. Registre a mensagem de erro e explique **quando** o Spring detecta o problema (na partida ou em execução?). Depois corrija.

---

## Etapa 5 — `@Query` com JPQL (Prata)

Acrescente ao `TaskRepository`:

```java
@Query("""
       select t from Task t
       where t.status <> :concluida
         and t.dueDate < :hoje
       order by t.dueDate
       """)
List<Task> buscarAtrasadas(@Param("concluida") TaskStatus concluida,
                           @Param("hoje") LocalDate hoje);
```

Imports necessários:

```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
```

Utilize no service:

```java
public List<Task> listarAtrasadas() {
    return repository.buscarAtrasadas(TaskStatus.DONE, LocalDate.now());
}
```

**Questões**

1. Por que a JPQL usa `Task` e `t.dueDate`, e não `tasks` e `due_date`?
2. Compare o SQL impresso com a JPQL escrita. O que o Hibernate acrescentou ou traduziu?
3. Se essa consulta fosse escrita como método derivado, qual seria o nome? Seria legível? Justifique a escolha por `@Query`.

**Aviso de segurança:** nunca monte consultas concatenando texto vindo do usuário. Use sempre parâmetros nomeados, que são tratados como dados. Este tema será aprofundado na Unidade 3.5.

> **Checkpoint Prata:** quatro endpoints de consulta derivada e um de `@Query` funcionando, com justificativas registradas.

---

## Etapa 6 — Relacionamento `User` 1:N `Task` (Ouro)

Agora o `User` da avaliação da semana 5 passa a ser uma entidade e ganha suas tarefas.

### 6.1 Entidade `User`

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "owner")
    private List<Task> tasks = new ArrayList<>();

    protected User() { }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // getters e setters
}
```

### 6.2 Lado dono na `Task`

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User owner;
```

Inclua `getOwner()` e `setOwner(User owner)`.

### 6.3 Repositório de usuários

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
}
```

### 6.4 Consultas que atravessam o relacionamento

No `TaskRepository`:

```java
List<Task> findByOwnerId(Long userId);

List<Task> findByOwnerIdAndStatus(Long userId, TaskStatus status);
```

### 6.5 Endpoints sugeridos

| Endpoint | Comportamento |
|---|---|
| `POST /users/{id}/tasks` | Cria uma tarefa já associada ao usuário |
| `GET /users/{id}/tasks` | Lista as tarefas do usuário |
| `GET /users/{id}/tasks?status=PENDING` | Filtra por status |

No service, ao criar a tarefa para um usuário, busque-o primeiro e associe:

```java
public Task criarParaUsuario(Long userId, Task task) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado: " + userId));
    task.setOwner(user);
    return taskRepository.save(task);
}
```

**Questões**

1. Qual tabela recebeu a coluna `user_id`? Por quê?
2. O que significa `mappedBy = "owner"`?
3. Onde no SQL aparece a **chave estrangeira**? Observe o `create table` impresso na partida.

> **Importante:** retorne sempre **DTOs** nos endpoints. Devolver `User` com `tasks` e `Task` com `owner` diretamente gera um ciclo infinito na serialização JSON.

> **Checkpoint Ouro:** criação de tarefas por usuário e listagem por usuário funcionando, com o `create table` explicado pela equipe.

---

## Etapa 7 — Desafio Diamante

### 7.1 Provocar e resolver o problema N+1

1. Cadastre ao menos 5 usuários com 2 tarefas cada.
2. Crie um endpoint que lista todos os usuários **com a quantidade de tarefas de cada um**, usando `findAll()` e percorrendo `user.getTasks().size()` dentro de um método do service.
3. Conte no console quantas instruções `select` foram executadas. Registre o número.

> Como a coleção é `LAZY`, o acesso fora de uma transação pode gerar `LazyInitializationException`. Se isso ocorrer, registre a mensagem: ela é a evidência do comportamento `LAZY`. O tema das transações será tratado na Unidade 3.7. Para o experimento, você pode anotar temporariamente o método do service com `@Transactional` e observar o N+1.

4. Resolva com `join fetch`:

```java
@Query("select distinct u from User u left join fetch u.tasks")
List<User> findAllWithTasks();
```

5. Repita a contagem de `select` e compare com o valor anterior.

### 7.2 Paginação

Adicione ao `TaskRepository`:

```java
Page<Task> findByStatus(TaskStatus status, Pageable pageable);
```

Exponha `GET /tasks/paginado?status=PENDING&page=0&size=3` e retorne um DTO com o conteúdo e o total de páginas.

### 7.3 DTOs sem ciclo

Crie os records abaixo (ou equivalentes) e converta as entidades no service:

```java
public record TaskResponse(Long id, String title, Priority priority,
                           TaskStatus status, LocalDate dueDate, Long ownerId) { }

public record UserResponse(Long id, String name, String email, int totalTasks) { }
```

> **Checkpoint Diamante:** relatório com contagem de `select` antes e depois do `join fetch`, paginação funcionando e nenhum endpoint devolvendo entidade diretamente.

---

## 8. Solução de problemas

| Sintoma | Causa provável | O que fazer |
|---|---|---|
| `No default constructor for entity` | Falta construtor sem argumentos | Adicionar `protected Task() {}` |
| `No property 'x' found for type 'Task'` | Nome do método derivado não corresponde a um atributo | Conferir grafia e maiúsculas do atributo |
| `Table "TASKS" not found` | Esquema não foi criado | Verificar `ddl-auto=create-drop` |
| `LazyInitializationException` | Coleção `LAZY` acessada fora do contexto | Usar `join fetch` ou converter para DTO dentro do service |
| Resposta JSON que nunca termina | Entidades bidirecionais serializadas | Retornar DTOs |
| `Parameter not bound` | `@Param` diferente do nome usado na consulta | Alinhar os nomes |
| Erro de coluna `NULL not allowed` | Campo `nullable = false` sem valor | Preencher o campo ou revisar a regra |

**Dica profissional:** ao ler uma pilha de erros, procure a linha `Caused by`. A causa real geralmente é a última dessa cadeia.

---

## 9. Checklist de entrega da equipe

- [ ] `Task` e `User` mapeadas como entidades com `jakarta.persistence`
- [ ] `TaskRepository` e `UserRepository` como interfaces `JpaRepository`
- [ ] Pelo menos 4 consultas derivadas e 1 `@Query` funcionando
- [ ] Relacionamento `User` 1:N `Task` com `mappedBy` e `@JoinColumn`
- [ ] Endpoints devolvendo DTOs
- [ ] Quadro da Etapa 3 preenchido e respostas de reflexão redigidas
- [ ] Comparativo de `select` antes e depois do `join fetch` (nível Diamante)

**Entregável:** repositório do projeto atualizado, acompanhado de um breve relatório em Markdown contendo os quadros e as respostas às questões de cada etapa.

---

## 10. Desafios extras (opcional)

1. Configure o **PostgreSQL** no lugar do H2. O que precisou mudar? O que permaneceu idêntico nas entidades e repositórios?
2. Altere `ddl-auto` para `update` e reinicie duas vezes com um banco em arquivo (`jdbc:h2:file:./data/tarefasdb`). Observe o que acontece com os dados e discuta por que `update` não é recomendado em produção.
3. Escreva uma consulta nativa (`nativeQuery = true`) equivalente à consulta de atrasadas e compare legibilidade e portabilidade.

---

## 11. Vínculo com as próximas semanas

- **Semana 7 (Unidade 3.5):** o campo `email` e as regras do `User` receberão **Bean Validation**, e as senhas passarão por **BCrypt**.
- **Semana 9 (Unidade 3.7):** o `@Transactional`, mencionado nas etapas 7.1, será estudado em profundidade.

---

## Referências

- WALLS, C. *Spring in Action*. 5. ed. Manning Publications, 2019.
- SPRING. *Spring Data JPA Reference Documentation*. Disponível em: https://docs.spring.io/spring-data/jpa/reference/.
- SPRING. *Guias do Spring Framework*. Disponível em: https://spring.io/guides.
