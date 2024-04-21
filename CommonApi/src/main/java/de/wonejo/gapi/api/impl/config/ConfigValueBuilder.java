package de.wonejo.gapi.api.impl.config;

import de.wonejo.gapi.api.config.IConfigValueBuilder;
import de.wonejo.gapi.api.config.serializer.IConfigValueSerializer;

public final class ConfigValueBuilder<T> implements IConfigValueBuilder<T> {

    public static<B> IConfigValueBuilder<B> of ( String pKey, IConfigValueSerializer<B> pSerializer ) {
        return new ConfigValueBuilder<>(pKey, pSerializer);
    }

    private final String key;
    private final IConfigValueSerializer<T> serializer;
    private String comment = "";
    private T defaultValue;

    private ConfigValueBuilder ( String pKey, IConfigValueSerializer<T> pSerializer ) {
        this.key = pKey;
        this.serializer = pSerializer;
    }

    public IConfigValueBuilder<T> comment(String pComment) {
        this.comment = pComment;
        return this;
    }

    public IConfigValueBuilder<T> defaultValue(T pDefaultValue) {
        this.defaultValue = pDefaultValue;
        return this;
    }

    public ConfigValue<T> build() {
        return new ConfigValue<>(this.serializer, this.key, this.comment, this.defaultValue);
    }
}
