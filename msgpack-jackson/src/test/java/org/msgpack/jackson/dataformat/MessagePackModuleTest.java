package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;

/**
 * @author marenzo
 * @since 2016-05-06
 */
public class MessagePackModuleTest {

  private ComplexPojo complexPojo;
  private ObjectMapper objectMapper;

  @Before
  public void setup() {
    objectMapper = new ObjectMapper(new MessagePackFactory());
    objectMapper.setAnnotationIntrospector(new JsonArrayFormat());
    objectMapper.registerModule(new MessagePackModule());

    complexPojo = new ComplexPojo("komamitsu", 20, singletonList("hello"), singletonMap("math", "hello"));
  }

  @Test
  public void testMapWithStringValue() throws Exception {
    Map<ComplexPojo, String> map = new HashMap<>();
    map.put(complexPojo.withAge(10), "hello");
    map.put(complexPojo.withAge(30), "world");

    byte[] bytes = objectMapper.writeValueAsBytes(map);
    JavaType javaType = objectMapper.constructType(new TypeReference<Map<ComplexPojo, String>>() {}.getType());
    Map<ComplexPojo, String> newValue = objectMapper.readValue(bytes, javaType);

    assertEquals(map.keySet(), newValue.keySet());
  }

  @Test
  public void testMapWithObjectValue() throws Exception {
    Map<ComplexPojo, SimplePojo> map = new HashMap<>();
    map.put(complexPojo.withAge(10), new SimplePojo("hello"));
    map.put(complexPojo.withAge(30), new SimplePojo("world"));

    byte[] bytes = objectMapper.writeValueAsBytes(map);
    JavaType javaType = objectMapper.constructType(new TypeReference<Map<ComplexPojo, SimplePojo>>() {}.getType());
    Map<ComplexPojo, SimplePojo> newValue = objectMapper.readValue(bytes, javaType);

    assertEquals(map.keySet(), newValue.keySet());
  }

  @Test
  public void testMapWithPrimitiveValue() throws Exception {
    Map<ComplexPojo, Integer> map = new HashMap<>();
    map.put(complexPojo.withAge(10), 10);
    map.put(complexPojo.withAge(30), 30);

    byte[] bytes = objectMapper.writeValueAsBytes(map);
    JavaType javaType = objectMapper.constructType(new TypeReference<Map<ComplexPojo, Integer>>() {}.getType());
    Map<ComplexPojo, Integer> newValue = objectMapper.readValue(bytes, javaType);

    assertEquals(map.keySet(), newValue.keySet());
  }

  @Test
  public void testMapWithNestedMapValue() throws Exception {
    Map<ComplexPojo, Map<String, String>> map = new HashMap<>();
    map.put(complexPojo.withAge(10), singletonMap("hello", "world"));
    map.put(complexPojo.withAge(30), singletonMap("world", "hello"));

    byte[] bytes = objectMapper.writeValueAsBytes(map);
    JavaType javaType = objectMapper.constructType(new TypeReference< Map<ComplexPojo, Map<String, String>>>() {}.getType());
    Map<ComplexPojo, Map<String, String>> newValue = objectMapper.readValue(bytes, javaType);

    assertEquals(map.keySet(), newValue.keySet());
  }

  @Test
  @Ignore
  public void testMapWithNestedMapObjectValue() throws Exception {
    Map<ComplexPojo, Map<SimplePojo, String>> map = new HashMap<>();
    map.put(complexPojo.withAge(10), singletonMap(new SimplePojo("hello"), "world"));
    map.put(complexPojo.withAge(30), singletonMap(new SimplePojo("world"), "hello"));

    byte[] bytes = objectMapper.writeValueAsBytes(map);
    JavaType javaType = objectMapper.constructType(new TypeReference<Map<ComplexPojo, Map<SimplePojo, String>>>() {}.getType());
    Map<ComplexPojo, Map<SimplePojo, String>> newValue = objectMapper.readValue(bytes, javaType);

    assertEquals(map.keySet(), newValue.keySet());
  }

  public static class SimplePojo {
    public String name;

    public SimplePojo() {
    }

    public SimplePojo(final String name) {
      this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final SimplePojo that = (SimplePojo) o;
      return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name);
    }
  }

  public static class ComplexPojo {
    public String name;
    public int age;
    public List<String> values;
    public Map<String, String> grades;

    public ComplexPojo() {
    }

    public ComplexPojo(final String name, final int age, final List<String> values, final Map<String, String> grades) {
      this.name = name;
      this.age = age;
      this.values = values;
      this.grades = grades;
    }

    public ComplexPojo withAge(final int age) {
      return new ComplexPojo(name, age, values, grades);
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final ComplexPojo that = (ComplexPojo) o;
      return age == that.age &&
        Objects.equals(name, that.name) &&
        Objects.equals(values, that.values) &&
        Objects.equals(grades, that.grades);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, age, values, grades);
    }
  }
}
