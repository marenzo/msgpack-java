package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.MapType;

/**
 * @author marenzo
 * @since 2016-05-05
 */
public class MessagePackModule extends Module {

  private static final MessagePackKeySerializer msgPackKeySerializer = new MessagePackKeySerializer();

  @Override
  public String getModuleName() {
    return "MessagePackModule";
  }

  @Override
  public Version version() {
    return PackageVersion.VERSION;
  }

  @Override
  public void setupModule(final SetupContext context) {
    context.addKeySerializers(new MessagePackSerializers());
    context.addDeserializers(new MessagePackDeserializers());
  }

  private static class MessagePackSerializers extends Serializers.Base {

    @Override
    public JsonSerializer<?> findSerializer(final SerializationConfig config, final JavaType type,
                                            final BeanDescription beanDesc) {
      return msgPackKeySerializer;
    }
  }

  private static class MessagePackDeserializers extends Deserializers.Base {

    @Override
    public JsonDeserializer<?> findMapDeserializer(final MapType type, final DeserializationConfig config,
                                                   final BeanDescription beanDesc,
                                                   final KeyDeserializer keyDeserializer,
                                                   final TypeDeserializer elementTypeDeserializer,
                                                   final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      return new MessagePackMapDeserializer(type);
    }
  }
}
