# Gabarito — Atividade Prática, Parte 1

**Semana 2 — Servidores de Aplicações Corporativas**
**Objetivo da atividade:** observar na prática o comportamento do servidor embarcado do Spring Boot.

Este gabarito apresenta duas formas de executar a atividade:

- **Via Docker Compose** (recomendada para a correção/demonstração em aula — não depende de Maven/Java instalados na máquina);
- **Via IDE local**, exatamente como descrito no roteiro original, para referência.

O projeto de exemplo já está pronto nesta pasta (`atividade-servidor-embarcado/`), gerado como se tivesse vindo do [start.spring.io](https://start.spring.io) com a dependência **Spring Web**.

---

## 1. Estrutura do projeto gerado

```
atividade-servidor-embarcado/
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── pom.xml
└── src/main/
    ├── java/br/edu/ifrn/atividadeservidorembarcado/
    │   └── AtividadeServidorEmbarcadoApplication.java
    └── resources/
        └── application.properties
```

Isso corresponde exatamente ao que o start.spring.io geraria com:

- **Project:** Maven
- **Language:** Java
- **Spring Boot:** 3.3.x
- **Dependencies:** Spring Web

Foi adicionado apenas um `@RestController` simples (`GET /`) para facilmente confirmar que o servidor está respondendo.

---

## 2. Subindo com Docker Compose

Não é preciso ter Maven nem Java instalados na máquina — apenas Docker.

```bash
cd atividade-servidor-embarcado
docker compose up --build
```

O que acontece por trás (explicar em aula):

1. O **Dockerfile** é multi-stage:
   - **Etapa 1 (`build`)**: usa uma imagem com Maven + JDK 17 para rodar `mvn clean package`, gerando o `.jar` executável (o mesmo fat JAR discutido no slide "Curiosidade: por que empacotar o servidor dentro do JAR?");
   - **Etapa 2 (final)**: usa uma imagem enxuta, só com o **JRE**, e copia apenas o `.jar` gerado. Nenhum servidor é instalado nesta imagem — ele já está dentro do JAR.
2. O `docker-compose.yml` builda essa imagem e expõe a porta `8080` do container para a porta `8080` da máquina.

### Saída esperada no terminal

```
atividade-servidor-embarcado  |   .   ____          _            __ _ _
atividade-servidor-embarcado  |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
atividade-servidor-embarcado  | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
atividade-servidor-embarcado  |  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
atividade-servidor-embarcado  |   '  |____| .__|_| |_|_| |_\__, | / / / /
atividade-servidor-embarcado  |  =========|_|==============|___/=/_/_/_/
atividade-servidor-embarcado  |
atividade-servidor-embarcado  |  :: Spring Boot ::                (v3.3.4)
atividade-servidor-embarcado  |
... Starting AtividadeServidorEmbarcadoApplication v0.0.1-SNAPSHOT using Java 17...
... Tomcat initialized with port 8080 (http)
... Starting service [Tomcat]
... Starting Servlet engine: [Apache Tomcat/10.1.30]
... Initializing Spring embedded WebApplicationContext
... Tomcat started on port 8080 (http) with context path '/'
... Started AtividadeServidorEmbarcadoApplication in 0.811 seconds (process running for 1.069)
```

Em outro terminal, confirme que a aplicação responde:

```bash
curl http://localhost:8080/
# Servidor embarcado no ar!
```

Para encerrar:

```bash
docker compose down
```

---

## 3. Respostas esperadas (roteiro original)

Confrontando com o roteiro da Parte 1:

| Pergunta do roteiro | Resposta (a partir do log acima) |
|---|---|
| Em qual **porta** o servidor subiu? | **8080** — linha `Tomcat initialized with port 8080 (http)` e depois confirmada em `Tomcat started on port 8080 (http)` |
| Qual o **nome e a versão** do servidor embarcado? | **Apache Tomcat 10.1.30** — linha `Starting Servlet engine: [Apache Tomcat/10.1.30]` (a versão exata varia conforme a versão do Spring Boot escolhida no Initializr) |
| Quanto tempo levou para **iniciar**? | Ver linha `Started AtividadeServidorEmbarcadoApplication in 0.811 seconds` — variará entre execuções e máquinas, mas tipicamente **menos de 2 segundos** |

<div class="curiosidade">

💡 Ponto para reforçar em aula: nenhum desses três dados (porta, servidor, tempo de start) foi configurado manualmente — tudo isso é **autoconfiguração** do Spring Boot a partir da simples presença da dependência `spring-boot-starter-web`.

</div>

---

## 4. Alternativa — execução local (sem Docker), como no roteiro original

Caso a turma prefira (ou não tenha Docker disponível):

```bash
cd atividade-servidor-embarcado
./mvnw spring-boot:run
```

ou, importando o projeto na IDE, executando a classe `AtividadeServidorEmbarcadoApplication` (método `main`) diretamente — os logs no console serão equivalentes aos mostrados na seção 2, apenas sem o prefixo `atividade-servidor-embarcado |` do Docker Compose.

---

## 5. Pontos de atenção para o professor durante a correção

- **Todo grupo deve identificar a versão do Tomcat nos logs**, não apenas "que tem um Tomcat". Isso reforça que o Spring Boot não usa um Tomcat "genérico e invisível" — é uma dependência versionada e rastreável do projeto (ver `spring-boot-starter-tomcat` no `pom.xml` efetivo, herdada transitivamente de `spring-boot-starter-web`).
- Se algum grupo reportar **outra porta** (ex.: erro de "Port 8080 already in use"), é uma boa oportunidade para discutir `server.port` no `application.properties` — mostra que a porta é configuração, não algo fixo do framework.
- Ao rodar via Docker, reforce que **quem expõe a porta para fora do container é o `docker-compose.yml`** (`ports: "8080:8080"`), não o Spring Boot — o Tomcat embarcado continua ouvindo a mesma porta interna independentemente de como a aplicação é executada.
