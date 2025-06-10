# Locadora DS
Por Lucas Eduardo, Lucas Honda e Nathaly Pereira

## Gerenciamento de erro (error handling)

Estratégias e técnicas utilizadas para lidar com situações inesperadas ou desvios que podem ocorrer durante o funcionamento de um sistema, processo ou atividade.

Exemplo com try/catch:

![](https://hc-cdn.hel1.your-objectstorage.com/s/v3/a8e7ee64813713fee394b9f68cdf95fd87893b2b_image.png)

O try contém o código que pode gerar uma exceção, e o catch contém o código que será executado se uma exceção for lançada no bloco try. 

## Fallback

Fallback, ou contingência, é uma abordagem para lidar com falhas ou erros em um sistema, fornecendo uma alternativa quando a lógica principal não pode ser executada.

## Padrão de projeto de software (Software design pattern)

Um padrão de desenho ou padrão de projeto é uma solução geral para um problema que ocorre com frequência dentro de um determinado contexto no projeto de software. 

## DAO Pattern

O padrão DAO (Data Access Object), em português "Objeto de Acesso a Dados", é um padrão de projeto que separa a lógica de negócios da lógica de acesso aos dados, encapsulando as operações de banco de dados em objetos próprios.

### O que consiste um arquivo DAO?

Consiste em uma série de métodos que, de maneira sanitizada, isto é, prevenindo SQL Injection, acessa e faz CRUD no banco de dados.

### O que é SQL Injection?

Uma vulnerabilidade de segurança que ocorre quando um invasor manipula a entrada do usuário para inserir ou modificar consultas SQL, potencialmente obtendo acesso a dados confidenciais ou manipulando o banco de dados.

### O que é CRUD?

CRUD é a sigla para Create (criar), Read (ler), Update (atualizar) e Delete (excluir), e representa as quatro operações básicas que são fundamentais em qualquer sistema que interage com um banco de dados.

Aprenda mais: https://www.oracle.com/java/technologies/dataaccessobject.html

## MVC Pattern

O padrão MVC (Model View Controller), é um padrão de design de software que separa um aplicativo em três componentes interconectados: o Modelo, a Visualização e o Controlador (Model, View e Controller).

Aprenda mais: https://developer.mozilla.org/en-US/docs/Glossary/MVC

### Model

Responsável por gerenciar os dados da aplicação, incluindo leitura, escrita e validação. É a camada que interage com o DAO. Responsável pelos Getters e Settters.

![](https://hc-cdn.hel1.your-objectstorage.com/s/v3/d87270ad758e26d41d385ab1382d829e5ea09745_image.png)

### View

Responsável por apresentar os dados do modelo ao usuário. É a interface gráfica que o utilizador vê e interage.

## Factory method pattern

Um padrão de projeto que utiliza métodos de fábrica para lidar com o problema de criar objetos sem precisar especificar suas classes exatas. Em vez de chamar um construtor, isso é feito invocando um método de fábrica para criar um objeto.


## BigDecimal

Diferente dos tipos primitivos, BigDecimal permite a representação e manipulação de números decimais com uma precisão arbitrária, eliminando os problemas de arredondamento e precisão que são comuns com o uso de double.


## Expressão regular (Regular expression / Regex)

Uma Expressão Regular (ou Regex) é um padrão (ou filtro) que descreve um conjunto de strings que corresponde ao padrão.
