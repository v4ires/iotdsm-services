# **I**nternet **o**f **T**hings **D**ata as a **S**ervice **M**odule (IoTDSM)

<p align="center"><img src="iot-dsm-logo.png"/></p>

O  **I**nternet **o**f **T**hings **D**ata as a **S**ervice **M**odule (IoTDSM) é um projeto desenvolvido no Laboratório de Sistemas Distribuídos e Programação Concorrente (LaSDPC) da Universidade de São Paulo (USP).
Este projeto tem como objetivo simplificar a aquisição e armazenamento de dados de sensores no contexto de Internet das Coisas.
Para isso, o IoTDSM oferece suporte à diferentes estratégias de armazenamento em bancos de dados SQL e NoSQL. Além de fornecer uma API RESTful para comunicação e gerenciamento do módulo.

## Getting Started

O IoTDSM tem como objetivo fornecer dados de objetos pertencentes a um ou mais ambientes inteligentes no contexto de IoT.
Para isso, esta ferramenta fornece uma interface simplificada para conexão com diferentes fontes de dados de redes de sensores, fornecendo suporte a diferentes tipos de bancos de dados, sejam eles SQL ou NoSQL.

### Installing

Para instalação do IoTDSM, basta compilar o projeto com o comando:

```bash
#Compila o projeto com todas as dependências desconsiderando a executação de testes
~$ gradle build fatJar -x test
```

Esse comando gerará um arquivo .jar com todas as dependências necessárias para execução do projeto.
O arquivo .jar gerado fica localizado no diretório:

```bash
#Arquivo: iot-repository-all-1.0-SNAPSHOT.jar
~$ build/libs  
```

Para executar o servidor RESTFul API do IoTDSM basta executar o comando:

```bash
#Executa Serviço RESTFul API do IoTDSM
~$ java -cp iot-repository-all-1.0-SNAPSHOT.jar EmbeddedServletMain <args>
```

## Running Tests

Para executar os testes unitários basta executar o comando:

```bash
#Executa Testes Unitários
~$ gradle test
```

## Running and Deployment

O IoTDSM provê uma interface de entrada para diferentes parâmetros de configuração.
Esses parâmetros são passados por meio de *flags* definidas pelo sistema.
Para visualizar os parâmetros disponíveis execute o comando:

```bash
#Mostra as opções de parâmetros disponíveis
~$ java -cp iot-repository-all-1.0-SNAPSHOT.jar EmbeddedServletMain -help
```

Os parâmetros diponíveis são os seguintes:

```bash
 -c,--configuration <arg>   Caminho para o arquivo de configuracao [config.properties].
 -h,--help                  Mostrar ajuda [true, false].
 -l,--log <arg>             Habilitar ou desabilitar log [true, false].
 -lf,--logfile <arg>        Arquivo de Configuração do Log4J [log4j.properties].
 -v,--log-level <arg>       Muda o nível do log [OFF, TRACE, trace, DEBUG, WARN, ERROR, FATAL, ALL].
```
Além dos parâmetros padrões outras configurações podem ser definidas por meio de um arquivo de configuração.
Este arquivo especifica a configuração do Banco de Dados e Servidor Web do IoTDSM.
A seguir é apresentado a tabela de argumentos disponíveis.

**Table 1**: Variáveis de entrada do Servidor Web.

|          Variável         |                   Descrição                  |
|:-------------------------:|:--------------------------------------------:|
| HOST                      | Endereço do Host do Banco de Dados           |
| PORT                      | Porta do Host do Banco de Dados              |
| DATABASE                  | Nome do Banco de Dados                       |
| USER                      | Nome do Usuário do Banco de Dados            |
| PASSWORD                  | Senha do Banco de Dados                      |
| DRIVER                    | Driver JDBC                                  |
| SQL_DEBUG                 | SQL Debug Mode                               |
| DATABASETYPE              | Tipo do Banco de Dados (mysql, pgsql, mongo) |
| USEHIBERNATE              | Usar Hibernate                               |
| SPARK_THREAD_POOL         | Usar Pool de Threads no Servidor             |
| SPARK_THREAD_POOL_TIMEOUT | Timeout do Pool de Threads do Servidor Web   |
| SPARK_THREAD_POOL_MIN     | Min Pool Size do Servidor Web                |
| SPARK_THREAD_POOL_MAX     | Max Pool Size do Servidor Web                |
| APIPORT                   | Porta do Servidor Web                        |
| DIALECT                   | Dialeto do Banco de Dados                    |

Essas variáveis são passadas por meio de um arquivo de configuração (config.properties) através do comando **-c=${config_file}**.

```bash
~$ java -cp iot-repository-all-1.0-SNAPSHOT.jar EmbeddedServletMain -c=${config_file}
```

## Docker

Para compilar as imagens em Docker do IoTDSM basta executar o comando:

```bash
#Para Compilar Imagem com o Banco de Dados MySQL
~$ docker build -f mysql.dockerfile -t iot-dsm/mysql .
```

ou

```bash
#Para Compilar Imagem com o Banco de Dados PostgreSQL
~$ docker build -f pgsql.dockerfile -t iot-dsm/pgsql .
```

ou

```bash
#Para Compilar Imagem com o Banco de Dados MongoDB
~$ docker build -f mongo.dockerfile -t iot-dsm/mongo .
```

Por fim, para executar a imagem desejada basta executar o comando:

```bash
#Para executar o IoTDSM dado um tipo de Banco de Dados
~$ docker run -d -p 8081:8081 iot-dsm/<db_type>:latest
```

## Built With

* [Gradle](https://gradle.org/) - Gerenciador de Dependências.

## Documentation

O IoTDSM disponibiliza uma página web com uma documentação detalhada sobre API RESTFul do sistema.
Para acessar esta documentação basta acessar o link:

```url
http://<HOST>:<PORT>/index.html
```

Além disso, para mais informações sobre o projeto visite a página [WIKI](https://github.com/v4ires/iot-repository/wiki) do projeto.

## Contributing

Por favor leiam o arquivo [CONTRIBUTING.md](CONTRIBUTING.md) para mais detalhes sobre como contribuir com este projeto.

## Authors

* **Vinicius Aires Barros** - *Idealizador inicial do Projeto* - [@v4ires](https://github.com/v4ires)
* **Leonardo Beck Prates**  - *Colaborador* - [@leobeckp](https://github.com/leobeckp)

## License

Este projeto está licenciado sob a licença MIT - veja o arquivo  [LICENSE](LICENSE) para mais detalhes.

## Acknowledgments

* Universidade de São Paulo (USP)
* Instituto de Ciências Matemáticas e de Computação (ICMC)
* Laboratório de Sistemas Distribuídos e Programação Concorrente (LaSDPC)
