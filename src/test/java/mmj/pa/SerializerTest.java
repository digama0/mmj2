package mmj.pa;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class SerializerTest {

    @Test
    public void testSerializeObject() {
        var serializer = new TestSerializer();
        assertSimilar(new JSONObject("{\"myValue\":3}"),
                serializer.serialize(new MyObject(3)));
    }

    @Test
    public void testDeserializeJsonObject() {
        var serializer = new TestSerializer();
        assertEquals(new MyObject(3),
                serializer.deserialize(new JSONObject("{\"myValue\":3}")));
    }

    @Test
    public void testSerializeArray() {
        var serializer = new TestSerializer().array(MyObject[]::new);
        assertSimilar(new JSONArray("[]"), serializer.serialize(new MyObject[] { }));
    }

    @Test
    public void testDeserializeNonEmptyJsonArray() {
        var serializer = new TestSerializer().array(MyObject[]::new);
        assertArrayEquals( new MyObject[]{ new MyObject(1)}, serializer.deserialize(new JSONArray("[{\"myValue\":1}]")));
    }

    @Test
    public void testSerializeNonEmptyArray() {
        var serializer = new TestSerializer().array(MyObject[]::new);
        assertSimilar(new JSONArray("[{\"myValue\":1}]"), serializer.serialize(new MyObject[] {new MyObject(1)}));
    }

    @Test
    public void serializingNullThrowsException() {
        var serializer = new TestSerializer().array(MyObject[]::new);
        assertThrows(NullPointerException.class , () -> serializer.serialize(new MyObject[1]));
    }

    @Test
    public void testListSerializer() {
        var serializer = new TestSerializer().list();
        assertSimilar( new JSONArray("[]"), serializer.serialize(List.of()));
        assertThrows(NullPointerException.class, () -> serializer.serialize(List.of(null)));
        assertSimilar(new JSONArray("[{\"myValue\":1}]"), serializer.serialize(List.of(new MyObject(1))));
        assertEquals(List.of(new MyObject(1)) , serializer.deserialize(new JSONArray("[{\"myValue\":1}]")));
    }

    @Test
    public void testSetSerializer() {
        var serializer = new TestSerializer().set();
        assertSimilar(new JSONArray("[]"), serializer.serialize(Set.of()));
        assertThrows(NullPointerException.class, () -> serializer.serialize(Set.of(null)));
        assertSimilar(new JSONArray("[{\"myValue\":1}]"), serializer.serialize(Set.of(new MyObject(1))));
        assertEquals(Set.of(new MyObject(1)) , serializer.deserialize(new JSONArray("[{\"myValue\":1}]")));
    }

    @Test
    public void testMapSerializer() {
        var serializer = new TestSerializer().map();
        assertSimilar(new JSONObject("{}"), serializer.serialize(Map.of()));
        assertThrows(NullPointerException.class, () -> serializer.serialize(Map.of("abc", null)));
        assertSimilar(new JSONObject("{\"key\" : {\"myValue\":1}}"), serializer.serialize(Map.of("key", new MyObject(1))));
        assertEquals(Map.of("key", new MyObject(1)) , serializer.deserialize(new JSONObject("{\"key\" : {\"myValue\":1}}")));
    }


    class TestSerializer implements Serializer<MyObject> {

        @Override
        public MyObject deserialize(Object o) {
            if (!(o instanceof JSONObject)) {
                return null;
            }
            return new MyObject((Integer) ((JSONObject)o).get("myValue"));
        }

        @Override
        public Object serialize(MyObject value) {
            return new JSONObject("{" + "myValue:" + value.myValue + "}");
        }
    }

    class MyObject {

        MyObject(int myValue) {
            this.myValue = myValue;
        }
        int myValue;

        @Override
        public boolean equals(Object other) {
            return other instanceof MyObject
                    && ((MyObject) other).myValue == myValue;
        }
    }

    private static void assertSimilar(JSONObject expected, Object actual) {
        assertTrue("Expected " + expected + " but actual is " + actual,
                expected.similar(actual));
    }

    private static void assertSimilar(JSONArray expected, Object actual) {
        assertTrue("Expected " + expected + " but actual is " + actual,
                expected.similar(actual));
    }
}