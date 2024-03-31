package de.wonejo.gapi.api.impl.config;

import de.wonejo.gapi.api.config.IConfigFile;
import de.wonejo.gapi.api.config.IConfigProvider;
import de.wonejo.gapi.api.config.IConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;

public class ConfigFile implements IConfigFile {

    private static final Logger LOGGER = LoggerFactory.getLogger("Config");

    public static ConfigFile of (Path pConfigPath, String pFilename ) {
        File configFile = new File(pConfigPath.toFile(), pFilename + ".properties");
        return new ConfigFile(configFile, pFilename);
    }

    private final File configFile;
    private final String fileName;

    private IConfigProvider provider;
    private boolean broken = false;

    private ConfigFile ( File pConfigFile, String pFileName ) {
        this.configFile = pConfigFile;
        this.fileName = pFileName;
    }

    public void init() {
        this.save();

        if (!this.configFile.exists()) {
            try {
                LOGGER.warn("Creating configuration file: {}", this.fileName);
                this.createFile();
            } catch (IOException pException) {
                LOGGER.warn("Fail creating {} configuration file", this.fileName);
                LOGGER.trace(pException.toString());
                this.broken = true;
            }
        }

        this.provider.configurations().clear();

        if ( !this.isBroken() ) {
            try {
                LOGGER.debug("Loading configurations from: {}", this.configFile);
                this.load();
            } catch (IOException pException) {
                LOGGER.error("Error loading config in: {}", this.fileName);
                LOGGER.trace(pException.toString());
                this.broken = true;
            }
        }
    }

    public void load() throws IOException {
        Scanner reader = new Scanner(this.configFile);

        for ( int lineIndex = 1; reader.hasNextLine(); lineIndex++ ) {
            String line = reader.nextLine();

            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] configParts = line.split("=", 2);
            String key = configParts[0];
            String value = configParts[1];

            Object configValue = parseConfigValue(value);

            if ( configValue != null )  {
                this.provider.configurations().put(key, new ConfigValue<>(key, "", configValue));
            }
        }

        this.provider.defineConfigurations();
        this.provider.configurations().clear();
    }

    private Object parseConfigValue (String pValue) {
        if ( pValue.equalsIgnoreCase("true") || pValue.equalsIgnoreCase("false") )
            return Boolean.parseBoolean(pValue);

        try {
            return Integer.parseInt(pValue);
        } catch (NumberFormatException pEx) {
            try {
                return Double.parseDouble(pValue);
            } catch (NumberFormatException pEx2 ) {
                return pValue;
            }
        }
    }

    public void save() {
        if ( !this.isBroken() )
            this.provider.buildConfigurations();
    }

    private void createFile () throws IOException {
        String configFile = this.getFileContent();

        FileWriter writer = new FileWriter(this.configFile, StandardCharsets.UTF_8);
        writer.write(configFile);
        writer.close();
    }

    private String getFileContent () {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, IConfigValue<?>> configEntry : this.provider.configurations().entrySet()) {
            String comment = configEntry.getValue().comment();
            String key = configEntry.getKey();
            IConfigValue<?> value = configEntry.getValue();

            builder.append("#").append(comment).append("  ||  DEFAULT: ").append(value.defaultValue()).append("\n");
            builder.append(key).append("=").append(value.get()).append("\n");
        }

        return builder.toString();
    }

    public ConfigFile provider (IConfigProvider pProvider )  {
        this.provider = pProvider;
        return this;
    }

    public boolean isBroken() {
        return this.broken;
    }
}