---
marp: true
theme: gaia
paginate: true
backgroundColor: #fff
class: lead
style: |
  section {
    font-size: 26px;
  }
  section.lead h1 {
    font-size: 46px;
  }
  table {
    font-size: 22px;
    margin: auto;
  }
  .columns {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
  }
  .box {
    border: 2px solid #4a4a4a;
    border-radius: 10px;
    padding: 12px;
    text-align: center;
  }
  .highlight {
    background-color: #fff3cd;
    border-left: 6px solid #e6a817;
    padding: 8px 14px;
    border-radius: 4px;
  }
  .curiosidade {
    background-color: #e7f3ff;
    border-left: 6px solid #2b7de9;
    padding: 8px 14px;
    border-radius: 4px;
  }
---

<!-- _class: lead -->

# Desenvolvimento de Sistemas Corporativos

## Semana 2 — Servidores de Aplicações Corporativas

Curso Superior de Tecnologia em Sistemas para Internet

---

## Recapitulando a Semana 1

Na aula anterior discutimos o papel dos sistemas corporativos no contexto empresarial:

- **SIG** — Sistemas de Informação Gerenciais
- **SAD** — Sistemas de Apoio à Decisão
- **ERP** — Enterprise Resource Planning

<br>

Hoje avançamos um nível na pilha: **onde e como essas aplicações corporativas efetivamente rodam.**

---

## Objetivos da aula

Ao final desta aula, o estudante será capaz de:

1. Explicar o conceito de **servidor de aplicação** e seu papel na arquitetura corporativa;
2. Comparar servidores de aplicação **tradicionais** (Java EE) com o modelo **embarcado** do Spring Boot;
3. Compreender **por que** e **como** o Spring Boot empacota um servidor dentro do próprio JAR executável.

---

## O que é um Servidor de Aplicação?

Um **servidor de aplicação** é um software que fornece o ambiente de execução para aplicações corporativas, oferecendo serviços prontos que a aplicação não precisa reimplementar:

<div class="columns">
<div class="box">
🔌<br><b>Gerenciamento de conexões</b><br>(HTTP, banco de dados)
</div>
<div class="box">
🔐<br><b>Segurança</b><br>autenticação e autorização
</div>
<div class="box">
🔄<br><b>Transações</b><br>controle transacional
</div>
<div class="box">
📨<br><b>Mensageria</b><br>filas e eventos
</div>
</div>

---

## Por que isso importa?

Sem um servidor de aplicação, cada equipe teria que **reinventar a roda** a cada projeto:

<div class="highlight">
Como abrir uma porta HTTP? Como gerenciar conexões concorrentes? Como controlar sessões de usuário? Como garantir que uma transação bancária não fique "pela metade"?
</div>

O servidor de aplicação existe justamente para **abstrair essa complexidade de infraestrutura**, permitindo que o desenvolvedor foque na **regra de negócio**.

---

## Papel na Arquitetura Corporativa

```
┌─────────────────────────────────────────────┐
│              CLIENTE (navegador, app)        │
└───────────────────────┬───────────────────────┘
                         │  HTTP / HTTPS
┌───────────────────────▼───────────────────────┐
│            SERVIDOR DE APLICAÇÃO               │
│  ┌───────────┐  ┌───────────┐  ┌────────────┐ │
│  │ Container  │  │ Segurança  │  │ Transações │ │
│  │  Web/EJB   │  │  (JAAS)    │  │   (JTA)    │ │
│  └───────────┘  └───────────┘  └────────────┘ │
│         Executa a APLICAÇÃO CORPORATIVA        │
└───────────────────────┬───────────────────────┘
                         │  JDBC / JPA
┌───────────────────────▼───────────────────────┐
│                BANCO DE DADOS                   │
└─────────────────────────────────────────────────┘
```

---

## Servidores de Aplicação Tradicionais (Java EE)

Antes do Spring Boot, o modelo dominante no mundo Java corporativo era:

- **WildFly** (antigo JBoss AS) — Red Hat
- **GlassFish** — implementação de referência Java EE / Jakarta EE
- **WebSphere** — IBM
- **WebLogic** — Oracle

**Características em comum:**
- Servidor instalado **separadamente** da aplicação;
- A aplicação é empacotada como **WAR** ou **EAR** e depois **implantada (deploy)** no servidor já em execução;
- Um único servidor pode hospedar **várias aplicações** simultaneamente.

---

## Fluxo tradicional de implantação

```
1. Desenvolver a aplicação  →  2. Empacotar em .war/.ear
        │
        ▼
3. Instalar/configurar o servidor (WildFly, GlassFish...)
        │
        ▼
4. Copiar o .war para a pasta "deployments" do servidor
        │
        ▼
5. Servidor detecta o arquivo e faz o deploy
        │
        ▼
6. Aplicação disponível em: servidor:porta/contexto
```

<div class="curiosidade">
💡 Curiosidade: em servidores como o WildFly, basta arrastar o .war para a pasta "standalone/deployments" que o próprio servidor percebe o novo arquivo e realiza o deploy automaticamente — mecanismo chamado de "deployment scanner".
</div>

---

## O modelo do Spring Boot: servidor embarcado

O Spring Boot inverte a lógica tradicional:

<div class="columns">
<div class="box">
<b>Modelo tradicional</b><br><br>
Servidor já instalado<br>⬇<br>
Aplicação é implantada <i>dentro</i> dele
</div>
<div class="box">
<b>Modelo Spring Boot</b><br><br>
Servidor (Tomcat) é uma <i>dependência</i><br>⬇<br>
Empacotado <i>dentro</i> da aplicação
</div>
</div>

Com a dependência `spring-boot-starter-web`, o projeto já traz o **Tomcat embarcado** — não é necessário instalar nenhum servidor separadamente.

---

## Exemplo prático — `pom.xml`

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

```java
@SpringBootApplication
public class SistemaCorporativoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SistemaCorporativoApplication.class, args);
    }
}
```

Ao executar `main()`, o próprio Tomcat embarcado **sobe automaticamente** — geralmente na porta `8080` — sem qualquer instalação prévia.

---

## Fluxo de implantação com Spring Boot

```
1. Desenvolver a aplicação
        │
        ▼
2. Empacotar com Maven/Gradle → gera um .jar executável
        │
        ▼
3. Executar: java -jar sistema-corporativo.jar
        │
        ▼
4. O Tomcat embarcado sobe junto com a aplicação
        │
        ▼
5. Aplicação já disponível em: localhost:8080
```

Note que **não existe um passo separado de "instalar o servidor"** — ele já está dentro do JAR.

---

## 💡 Curiosidade: por que empacotar o servidor dentro do JAR?

O Spring Boot gera o chamado **"fat JAR"** (ou **"uber JAR"**): um único arquivo `.jar` contendo:

- O código compilado da aplicação;
- Todas as bibliotecas (dependências) necessárias;
- O próprio servidor Tomcat (ou Jetty/Undertow, se configurado) embarcado.

<div class="curiosidade">
Isso segue a filosofia dos <b>12-Factor Apps</b>: a aplicação deve ser autocontida e executável de forma independente do ambiente ("build once, run anywhere"), o que facilita enormemente a implantação em containers (Docker) e ambientes de nuvem.
</div>

Basta ter o **Java Runtime (JRE)** instalado — nada de configurar servidor externo.

---

## Comparativo — Tradicional × Spring Boot Embarcado

| Aspecto | Servidor Tradicional (Java EE) | Spring Boot Embarcado |
|---|---|---|
| Instalação | Servidor instalado à parte | Nenhuma instalação separada |
| Empacotamento | `.war` / `.ear` | `.jar` executável (fat JAR) |
| Múltiplas apps por servidor | Sim, comum | Não — 1 app = 1 processo |
| Execução | Deploy em servidor já ativo | `java -jar aplicacao.jar` |
| Ideal para | Grandes ambientes corporativos centralizados | Microsserviços, containers, nuvem |
| Curva de configuração | Alta (configuração do servidor) | Baixa (convenção sobre configuração) |

---

## Quando cada abordagem faz mais sentido?

<div class="columns">
<div class="box">
🏢<br><b>Servidor Tradicional</b><br>
Ambientes legados, grandes datacenters corporativos, múltiplas aplicações Java EE compartilhando um mesmo servidor físico
</div>
<div class="box">
☁️<br><b>Spring Boot Embarcado</b><br>
Microsserviços, aplicações em containers (Docker/Kubernetes), times ágeis, deploy contínuo em nuvem
</div>
</div>

Na prática de mercado atual, o modelo embarcado é **dominante** em novos projetos, especialmente em arquiteturas de microsserviços.

---

## Trocando o servidor embarcado (curiosidade extra)

O Spring Boot **não obriga** o uso do Tomcat. É possível trocar por outro servidor embarcado apenas alterando a dependência:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
```

Isso mostra o quanto a arquitetura do Spring Boot é **modular e desacoplada**.

---

<!-- _class: lead -->

# Spring Framework
## De onde vem o Spring Boot

---

## O que é o Spring Framework?

O **Spring Framework** (2003) nasceu como resposta à complexidade excessiva do **J2EE/Java EE** da época — muito XML, muitas interfaces obrigatórias, EJBs pesados para tarefas simples.

Seus pilares desde o início:

- **Inversão de Controle (IoC)** — o framework cria e gerencia os objetos (beans), não o desenvolvedor;
- **Injeção de Dependência (DI)** — as dependências entre objetos são "injetadas" automaticamente;
- **Programação Orientada a Aspectos (AOP)** — permite isolar responsabilidades transversais (log, transação, segurança) do código de negócio.

O Spring virou o **padrão de facto** para desenvolvimento corporativo em Java, mesmo sem fazer parte da especificação oficial Java EE.

---

## Evolução do ecossistema Java corporativo

```
1999 — J2EE            2006 — Java EE           2017+ — Jakarta EE
  EJBs pesados,           Anotações,               Sob a Eclipse
  muito XML               melhorias graduais       Foundation (Oracle doou)

              2003 — Spring Framework
              Alternativa mais leve ao EJB
              IoC + DI + AOP
                        │
                        ▼
              2014 — Spring Boot
              Convenção sobre configuração
              Servidor embarcado, autoconfiguração
                        │
                        ▼
              Hoje — padrão dominante para
              microsserviços e nuvem
```

<div class="curiosidade">
💡 Curiosidade: o Spring não substituiu o Java EE por decreto — ele "venceu" na prática de mercado por ser mais simples de testar e configurar. Hoje o Spring inclusive reaproveita partes de especificações Jakarta EE (como Bean Validation e JPA) por baixo dos panos.
</div>

---

## Spring Framework × Spring Boot

<div class="columns">
<div class="box">
<b>Spring Framework</b><br><br>
O "motor": IoC, DI, AOP, Spring MVC, Spring Data...<br><br>
Exige configuração manual detalhada (XML ou Java Config)
</div>
<div class="box">
<b>Spring Boot</b><br><br>
Uma <i>camada sobre</i> o Spring Framework<br><br>
Autoconfiguração + servidor embarcado + "starters" prontos
</div>
</div>

Ou seja: **todo projeto Spring Boot é um projeto Spring Framework por baixo** — o Boot só remove o trabalho repetitivo de configuração.

---

## Outros frameworks do ecossistema Java

Para contextualizar o Spring Boot no cenário mais amplo:

| Framework | Característica principal |
|---|---|
| **Java EE / Jakarta EE** | Especificação oficial; implementada por WildFly, GlassFish, WebSphere |
| **Spring / Spring Boot** | Framework de mercado; não é uma especificação, mas o mais adotado |
| **Micronaut** | Similar ao Spring Boot, porém com inicialização mais rápida (menos reflexão em runtime) |
| **Quarkus** | Focado em nativo (GraalVM) e ambientes serverless/containers |

Spring Boot continua sendo o mais usado no mercado por sua **maturidade, documentação e comunidade**.

---

## O que é o start.spring.io?

O **[start.spring.io](https://start.spring.io)** (Spring Initializr) é um gerador de projetos Spring Boot oficial e mantido pela própria equipe do Spring.

Ele resolve o problema de **começar um projeto do zero**: em vez de montar manualmente a estrutura de pastas, o `pom.xml`/`build.gradle` e as dependências, você:

1. Escolhe a ferramenta de build (**Maven** ou **Gradle**);
2. Escolhe a linguagem (**Java**, Kotlin ou Groovy);
3. Escolhe a versão do **Spring Boot**;
4. Preenche metadados do projeto (grupo, artefato, nome do pacote);
5. Seleciona as **dependências** desejadas (ex.: Spring Web, Spring Data JPA, PostgreSQL Driver...);
6. Clica em **Generate** e baixa um `.zip` pronto para importar na IDE.

<div class="curiosidade">
💡 Curiosidade: o start.spring.io tem uma API REST por trás — as principais IDEs (IntelliJ, VS Code, Eclipse/STS) usam essa mesma API para gerar o projeto direto de dentro do editor, sem precisar abrir o navegador.
</div>

---

<!-- _class: lead -->

# Atividade Prática

## Explorando o servidor embarcado

---

## Atividade — Parte 1 (Laboratório, ~20 min)

**Objetivo:** observar na prática o comportamento do servidor embarcado.

1. Acesse [start.spring.io](https://start.spring.io) e gere um projeto com a dependência **Spring Web**;
2. Importe o projeto na sua IDE (IntelliJ, Eclipse ou VS Code);
3. Execute a classe principal (`main`) e observe o console;
4. Identifique nos logs:
   - Em qual **porta** o servidor subiu;
   - Qual é o **nome e a versão** do servidor embarcado (procure por "Tomcat");
   - Quanto tempo levou para a aplicação **iniciar** ("Started ... in ... seconds").

---

## Atividade — Parte 2 (Discussão em duplas, ~15 min)

Com o projeto em execução, responda em conjunto com um colega:

1. Gere o pacote final com `mvn clean package` (ou `./mvnw clean package`) e localize o arquivo `.jar` gerado na pasta `target/`;
2. Execute-o diretamente pelo terminal com `java -jar target/nome-do-arquivo.jar`;
3. **Reflita e anote:** em que momento, nesse processo, foi necessário instalar algum servidor separadamente? Compare mentalmente com o fluxo de deploy tradicional (WAR em um WildFly/GlassFish) apresentado nesta aula;
4. Cada dupla compartilha uma conclusão com a turma ao final.

---

<!-- _class: lead -->

# Próxima aula

## Unidade 3.1 e 3.2 — Arquitetura de aplicações corporativas e camadas de uma aplicação web

Estruturação de projetos Spring Boot em `controller`, `service`, `repository` e `model`.
