# Sistema de Biblioteca - Spring Boot e JPA

Projeto da atividade de consultas de uma biblioteca. O sistema modela as entidades solicitadas, persiste os relacionamentos com Spring Data JPA e disponibiliza as sete consultas por uma API REST.

## Requisitos

- Java 17 ou superior
- Maven 3.6 ou superior
- Porta `8080` disponível

O banco H2 é executado em memória. Não é necessário instalar ou configurar um servidor de banco de dados.

## Executar

No terminal, na pasta raiz do projeto:

```bash
mvn test
mvn spring-boot:run
```

Quando aparecer a mensagem de inicialização concluída, a API estará disponível em `http://localhost:8080`. Na primeira inicialização, o sistema cria dados de exemplo automaticamente.

Para gerar e executar o JAR:

```bash
mvn clean package
java -jar target/atividade-jpa-0.0.1-SNAPSHOT.jar
```

O banco é recriado a cada inicialização (`create-drop`), então os dados de exemplo voltam ao estado inicial quando a aplicação reinicia.

## Consultas da atividade

| Exercício | Requisição | Resultado |
|---|---|---|
| 1. Livros disponíveis | `GET /api/livros/disponiveis` | Livros com quantidade disponível maior que zero, em ordem de título. |
| 2. Livros por categoria | `GET /api/livros/categoria/{nomeCategoria}` | Livros da categoria informada, sem diferenciar maiúsculas de minúsculas. |
| 3. Usuários por nome | `GET /api/usuarios?nome={trecho}` | Usuários cujo nome contém o trecho informado, sem diferenciar maiúsculas de minúsculas. |
| 4. Empréstimos ativos de usuário | `GET /api/usuarios/{usuarioId}/emprestimos/ativos` | Empréstimos ativos do usuário, incluindo usuário, itens e livros. |
| 5. Livros por autor | `GET /api/livros/autor?nome={nome}` | Livros do autor informado, em ordem crescente de ano de publicação. |
| 6. Empréstimos atrasados | `GET /api/emprestimos/atrasados` | Empréstimos ATIVO com devolução prevista anterior à data atual, incluindo usuário e livros. |
| 7. Quantidade por categoria | `GET /api/categorias/contagem-livros` | Nome e total de livros por categoria, do maior total para o menor. Categorias sem livros também aparecem. |

Exemplos com `curl` (com a aplicação em execução):

```bash
curl http://localhost:8080/api/livros/disponiveis
curl http://localhost:8080/api/livros/categoria/Fantasia
curl -G http://localhost:8080/api/usuarios --data-urlencode "nome=Silva"
curl http://localhost:8080/api/usuarios/1/emprestimos/ativos
curl -G http://localhost:8080/api/livros/autor --data-urlencode "nome=Bruno Costa"
curl http://localhost:8080/api/emprestimos/atrasados
curl http://localhost:8080/api/categorias/contagem-livros
```

As respostas são JSON. O conjunto de demonstração inclui os usuários `Joao Silva` e `Maria Silva Santos`, livros nas categorias Fantasia, Tecnologia e Romance, e empréstimos ativo e atrasado. Os IDs gerados começam em `1` no banco limpo.

## Banco H2

O console do banco está em `http://localhost:8080/h2-console` enquanto a aplicação estiver em execução. Use estes dados de conexão:

- JDBC URL: `jdbc:h2:mem:biblioteca`
- Usuário: `sa`
- Senha: em branco

## Como o código está organizado

- `model`: entidades `Livro`, `Autor`, `Categoria`, `Usuario`, `Emprestimo` e `ItemEmprestimo`, além do enum de status. `Livro` e `Autor` têm associação muitos-para-muitos por `livro_autor`, com chave composta gerada pelo mapeamento JPA; `Livro` aponta para `Categoria`; `Emprestimo` aponta para `Usuario` e possui itens, cada um ligado a um livro.
- `repository`: interfaces Spring Data JPA. Consultas simples usam métodos derivados do nome; consultas com filtros relacionados, agregação e carregamento de relacionamentos usam JPQL e `@EntityGraph`.
- `controller`: endpoints REST que recebem parâmetros e chamam os repositórios.
- `dto`: `CategoriaLivrosResumo`, projeção da consulta agregada de livros por categoria.
- `config/DadosIniciais`: popula o H2 com dados de demonstração ao iniciar.
- `src/test`: testes com H2 verificam filtros, ordenação, relacionamentos dos empréstimos e agregação.

O campo `status` é persistido como texto (`ATIVO`, `DEVOLVIDO` ou `ATRASADO`). A consulta de atrasos segue as duas condições do enunciado: status `ATIVO` e data prevista menor que a data atual; ela não altera automaticamente o status para `ATRASADO`.

## Testes

Execute toda a suíte com:

```bash
mvn test
```