package edu.usp.icmc.lasdpc.deserialization;

import java.util.List;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros <viniciusaires@usp.br>
 */
public interface IDeserializer {
    /**
     * Função responsável por ler apenas uma medida do arquivo de dados
     *
     * @return Retorna um objeto de valor de medida.
     */
    Object readObject();

    /**
     * Função responsável por ler uma lista de medidas do arquivo de dados
     *
     * @return Retorna uma lista de objetos de valor de medida.
     */
    List<Object> readArray();

    /**
     * Função responsável por abrir o arquivo de dados contendo as medidas
     *
     * @return Retorna se o arquivo foi aberto com sucesso ou não
     */
    boolean loadContent(String... args);

    /**
     * Função responsável por fechar o arquivo de dados contendo as medidas
     */
    void close();
}
