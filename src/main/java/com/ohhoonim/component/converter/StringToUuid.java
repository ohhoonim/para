package com.ohhoonim.component.converter;

import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

public class StringToUuid implements Converter<String, UUID>{

    @Override
    @Nullable
    public UUID convert(String source) {
        if (source == null) {
            return null;
        }
        return UUID.fromString(source);
    }

    
}
