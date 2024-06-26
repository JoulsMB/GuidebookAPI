package de.wonejo.gapi.impl.cfg;

import com.google.common.collect.Maps;
import de.wonejo.gapi.api.cfg.IConfigProvider;
import de.wonejo.gapi.api.cfg.IConfigValue;
import de.wonejo.gapi.api.cfg.serializer.IConfigValueSerializer;

import java.util.Map;

public abstract class ConfigProvider implements IConfigProvider {

    private final Map<String, IConfigValue<?>> configurations = Maps.newHashMap();

    public <T> IConfigValue<T> createConfiguration ( IConfigValueSerializer<T> pSerializer, String pKey, T pValue )  {
        return this.createConfig(pSerializer, pKey, null, pValue);
    }

    public <T> IConfigValue<T> createConfig(IConfigValueSerializer<T> pSerializer, String pKey, String pComment, T pValue) {
        IConfigValue<T> value = ConfigValueBuilder.of(pKey, pSerializer).comment(pComment).defaultValue(pValue).build();
        this.configurations.put(pKey, value);
        return value;
    }

    public <T> IConfigValue<T> getOrDefault(String pKey, IConfigValue<T> pDefaultReturn) {
        IConfigValue<T> defaultConfig = getConfigById(pKey);
        return defaultConfig == null ? pDefaultReturn : defaultConfig;
    }

    @SuppressWarnings("unchecked")
    public <T> IConfigValue<T> getConfigById(String pKey) {
        return (IConfigValue<T>) this.configurations.get(pKey);
    }

    public Map<String, IConfigValue<?>> configurations() {
        return this.configurations;
    }
}
