package com.source.bundleboard.config.logging;

import com.fasterxml.jackson.core.JsonStreamContext;
import net.logstash.logback.mask.ValueMasker;

public class CustomDataMasker implements ValueMasker {

    private final RegexMaskerHelper regexMaskerHelper = new RegexMaskerHelper();

    public void setMaskPattern(String maskPattern) {
        regexMaskerHelper.addMaskPattern(maskPattern);
    }


    @Override
    public Object mask(JsonStreamContext jsonStreamContext, Object value) {
        if (value instanceof CharSequence) {
            return regexMaskerHelper.maskMessage(value.toString());
        }
        return value;
    }
}