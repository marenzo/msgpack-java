package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessagePackMapDeserializer extends StdDeserializer<Object>
{
    private final MapType mapType;
    private final ObjectMapper objectMapper;

    public MessagePackMapDeserializer(final MapType type)
    {
        super(Object.class);
        this.mapType = type;
        this.objectMapper = new ObjectMapper(new MessagePackFactory());

        objectMapper.setSerializerFactory(new MessagePackSerializerFactory());
        objectMapper.setAnnotationIntrospector(new JsonArrayFormat());
    }

    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final Map<Object, Object> result = new HashMap<>();

        final JavaType keyType = mapType.getKeyType();
        final JavaType valueType = mapType.getContentType();

        JsonToken jsonToken = jp.nextToken();
        Object lastKey = null;

        while (!Thread.currentThread().isInterrupted()) {
            if (jsonToken == JsonToken.END_OBJECT) {
                jp.nextToken();
                final Object valueDeserialized = objectMapper.readValue(jp, valueType);
                result.put(lastKey, valueDeserialized);
                break;
            }
            if (lastKey != null && jsonToken != null) {
                final Object valueDeserialized = objectMapper.readValue(jp, valueType);
                result.put(lastKey, valueDeserialized);
                jsonToken = jp.nextToken();
            }
            if (jsonToken == null) {
                break;
            }
            lastKey = objectMapper.readValue(jp, keyType.getRawClass());
            jsonToken = jp.nextToken();
        }
        return result;
    }
}
