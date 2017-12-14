## JMeter Commandline Execution

O Apache JMeter permite executar testes tanto via interface gráfica como também pelo terminal. 
Em seguida é mostrado um exemplo básico de como executar um workload test no JMeter via linha de comando:

```bash
~$ $JMETER_HOME/bin/jmeter -n -t ${jmx_file} -l ${output_csv} 
```

* As variáveis ***${jmx_file}*** e ***${output_csv}*** devem ser substituidas pelos respectivos *paths*. 

### Variáveis

Foram definidas variáveis para execução dinâmica de experimentos utilizando os arquivos de configuração de experimento *jmx*.
As seguintes variáveis foram definidas: 

**Table 1**: Relação de variáveis criadas

|       Variável      |           Descrição          |             Utilização            |
|:-------------------:|:----------------------------:|:---------------------------------:|
|        Thread       |       Número de Threads      |               Padrão              |
|        RampUp       | Threads por Unidade de Tempo |               Padrão              |
|        Repeat       |     Número de Replicações    |               Padrão              |
|       Protocol      |  Protocolo (ex. http, https) |               Padrão              |
|         Host        |      Workload Test Host      |               Padrão              |
|         Port        |           Host Port          |               Padrão              |
|         Path        |           URL Path           |               Padrão              |
|    RandomDelayMax   |     Tempo Máximo de Delay    |           Uniform Timer           |
| ConstantDelayOffset |     Constant Delay Offset    | Uniform, Poisson e Gaussian Timer |
|        Lambda       |            Lambda            |           Poisson Timer           |
|      Deviation      |           Deviation          |           Gaussian Timer          |

Para utilizar as variáveis como argumento no terminal basta acrescentar *-J${nome_variavel}* ao comando *$JMETER_HOME/bin/jmeter*.
Um exemplo de utilização de variáveis aos arquivos jmx utilizados é dado a seguir:

```bash
~$ $JMETER_HOME/bin/jmeter -n -t ${jmx_file} -l ${output_csv} -JProtocol ${protocol} -JHost ${host} -JPort ${port} -JPath ${path} -JThread ${num_threads} -JRampUp ${rampup}
```