package edu.usp.icmc.lasdpc;

import edu.usp.icmc.lasdpc.repositories.BaseRepository;
import edu.usp.icmc.lasdpc.utils.PropertiesReader;
import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class BaseMain {

    public static String _configFileName = "local-host.properties";
    public static String _log4jFile = "log4j.properties";
    public static String _logLevel = "ALL";
    private static Options options = new Options();
    private static final Logger log = LoggerFactory.getLogger(BaseMain.class);

    /**
     * Inicializa as configurações do Banco de Dados
     */
    static void initDatabaseConnection() {
        if (Boolean.parseBoolean(PropertiesReader.getValue("DB_ACTIVE"))) {
            BaseRepository.initializeConnections();
            log.info("Database Connection Enabled!");
        }
    }

    /**
     * Inicializa as configurações de sistema do IoTDSM.
     *
     * @param args Argumento de configurações passados pelo método ImportData
     */
    static void initOptions(String[] args) {
        options.addOption("c", "configuration", true, "Caminho para o arquivo de configuracao [config.properties].");
        options.addOption("l", "log", true, "Habilitar ou desabilitar log [true, false].");
        options.addOption("lf", "log-file", true, "Arquivo de Configuracao do Log4J [log4j.properties].");
        options.addOption("lv", "log-level", true, "Muda o nivel do log [OFF, TRACE, INFO, DEBUG, WARN, ERROR, FATAL, ALL].");
        options.addOption("h", "help", false, "Mostrar ajuda [true, false].");
        options.addOption("v", "version", false, "Mostrar a versão do sistema.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
            checkCMDOptions(cmd);
        } catch (ParseException e) {
            log.error(e.getMessage());
            showHelp();
        }
    }

    /**
     * Configura as propriedades do Servidor de Aplicação.
     */
    static void initServerProperties() {
        Path path = Paths.get(_configFileName);
        if (Files.exists(path)) {
            PropertiesReader.initialize(_configFileName);
            log.info("--------------------------");
            log.info("Config Properties File");
            log.info("--------------------------");
            log.info("HTTP API Port: {}", PropertiesReader.getValue("APIPORT"));

            if (Boolean.parseBoolean(PropertiesReader.getValue("DB_ACTIVE"))) {
                log.info("Database Type: {}", PropertiesReader.getValue("DATABASETYPE"));
                log.info("Hibernate is On: {}", PropertiesReader.getValue("USEHIBERNATE"));
                log.info("SQL Debug is On: {}", PropertiesReader.getValue("SQL_DEBUG"));
                log.info("Thread Pool is On: {}", Boolean.parseBoolean((PropertiesReader.getValue("SPARK_THREAD_POOL"))));
            }

            if (Boolean.parseBoolean((PropertiesReader.getValue("SPARK_THREAD_POOL")))) {
                log.info("Thread Pool Timeout: {}", Integer.parseInt((PropertiesReader.getValue("SPARK_THREAD_POOL_TIMEOUT"))));
                log.info("Thread Pool Min: {}", Integer.parseInt((PropertiesReader.getValue("SPARK_THREAD_POOL_MIN"))));
                log.info("Thread Pool Max: {}", Integer.parseInt((PropertiesReader.getValue("SPARK_THREAD_POOL_MAX"))));
            }
            log.info("--------------------------");
        } else {
            log.error("Arquivo de configuracoes nao encontrado no caminho \"{}\".", path);
        }
    }

    /**
     * Mostra as opções de argumentos do sistema.
     */
    static void showHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("EmbeddedServletMain", options);
        System.exit(0);
    }

    /**
     * Método que habilita a utilização do Log4J.
     */
    static void enableLog4J(String logLevel) {
        LogManager.getRootLogger().setLevel(Level.toLevel(_logLevel));
        Properties properties = PropertiesReader.initialize(_log4jFile);
        LogManager.getRootLogger().setLevel(Level.toLevel(logLevel));
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(properties);
    }

    /**
     * Método que desabilita a utilização do Log4J
     */
    static void disableLog4J() {
        LogManager.resetConfiguration();
    }

    /**
     * Método que recebe um cmd como parâmetro e executa os argumentos passados pelo usuário.
     *
     * @param cmd
     */
    static void checkCMDOptions(CommandLine cmd) {
        if (cmd.hasOption("h")) {
            showHelp();
            System.exit(0);
        } else if (cmd.hasOption("v")) {
            System.out.println("IoTDSM v" + EmbeddedServletMain.class.getPackage().getImplementationVersion());
            System.exit(0);
        } else {
            log.info("Starting IoT Repository Module...");
            if (cmd.hasOption("c")) {
                _configFileName = cmd.getOptionValue("c");
            }
            if (cmd.hasOption("l")) {
                if (Boolean.parseBoolean(cmd.getOptionValue("l"))) {
                    if (cmd.hasOption("lf")) {
                        _log4jFile = cmd.getOptionValue("lf");
                    }
                    if (cmd.hasOption("lv")) {
                        _logLevel = cmd.getOptionValue("lv");
                    }
                    enableLog4J(_logLevel);
                } else {
                    disableLog4J();
                }
            }
        }
    }
}
