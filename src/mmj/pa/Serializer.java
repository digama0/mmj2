//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * Serializer.java  0.01 1/11/2016
 */

package mmj.pa;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import org.json.*;

import mmj.lang.*;

public interface Serializer<T> {
    /**
     * Convert a JSON object into an object of type {@code T}. Intended to be
     * overridden; the default implementation just casts the object, and so is
     * only valid for settings of type {@link String}, {@link JSONArray},
     * {@link JSONObject} and primitive data types.
     *
     * @param o The stored JSON node, in any valid JSON type
     * @return The corresponding object of type {@code T}
     */
    T deserialize(final Object o);

    /**
     * Convert an object of the target type to a JSON node. Intended to be
     * overridden; the default implementation just casts the object, and so is
     * only valid for settings of type {@link String}, {@link JSONArray},
     * {@link JSONObject} and primitive data types.
     *
     * @param value The data to serialize
     * @return The data as a JSON node
     */
    Object serialize(final T value);

    public static <T> Serializer<T> of(final Function<Object, T> deser,
        final Function<T, Object> ser)
    {
        return new Serializer<T>() {
            @Override
            public T deserialize(final Object o) {
                return deser.apply(o);
            }

            @Override
            public Object serialize(final T value) {
                return ser.apply(value);
            }
        };
    }

    /**
     * Transform a serializer for the type {@code T} into a serializer for the
     * type {@code List<T>}, using a JSON array to store the values.
     *
     * @param generator An array constructor for the type {@code T}
     * @return a list serializer
     */
    default Serializer<T[]> array(final IntFunction<T[]> generator) {
        return Serializer.of(
            o -> ((JSONArray)o).stream().map(this::deserialize)
                .toArray(generator),
            v -> Arrays.stream(v).map(this::serialize)
                .collect(Collectors.toCollection(JSONArray::new)));
    }

    /**
     * Transform a serializer for the type {@code T} into a serializer for the
     * type {@code List<T>}, using a JSON array to store the values.
     *
     * @return a list serializer
     */
    default Serializer<List<T>> list() {
        return Serializer.of(
            o -> ((JSONArray)o).stream().map(this::deserialize)
                .collect(Collectors.toList()),
            v -> v.stream().map(this::serialize)
                .collect(Collectors.toCollection(JSONArray::new)));
    }

    /**
     * Transform a serializer for the type {@code T} into a serializer for the
     * type {@code Set<T>}, using a JSON array to store the values.
     *
     * @return a set serializer
     */
    default Serializer<Set<T>> set() {
        return Serializer.of(
            o -> ((JSONArray)o).stream().map(this::deserialize)
                .collect(Collectors.toSet()),
            v -> v.stream().map(this::serialize)
                .collect(Collectors.toCollection(JSONArray::new)));
    }

    /**
     * Transform a serializer for the type {@code T} into a serializer for the
     * type {@code Map<String, T>}, using a JSON object to store the mappings.
     *
     * @return a map serializer
     */
    default Serializer<Map<String, T>> map() {
        return Serializer.of(
            o -> ((JSONObject)o).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                    e -> deserialize(e.getValue()))),
            v -> v.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                    e -> serialize(e.getValue()), (a, b) -> a,
                    JSONObject::new)));
    }

    /**
     * Transform a serializer for the type {@code T} into a serializer for the
     * type {@code List<T>}, using a JSON array to store the values.
     *
     * @return a list serializer
     */
    default Serializer<Optional<T>> opt() {
        return Serializer.of(
            o -> (o == JSONObject.NULL ? Optional.empty()
                : Optional.of(deserialize(o))),
            v -> v.map(this::serialize).orElse(JSONObject.NULL));
    }

    public static <T extends Enum<T>> Serializer<T> getEnumSerializer(
        final Class<T> clazz)
    {
        return Serializer.of(o -> Enum.valueOf(clazz, (String)o),
            v -> v.toString());
    }

    public static Serializer<File> getFileSerializer(
        final Supplier<File> path)
    {
        return Serializer.of(o -> new File(path.get(), (String)o),
            v -> path.get() == null ? v.toString()
                : path.get().toPath().relativize(v.toPath()).toString());
    }

    public static final Serializer<Color> COLOR_SERIALIZER = Serializer
        .of(o -> {
            final JSONArray a = (JSONArray)o;
            try {
                return new Color(a.getInt(0), a.getInt(1), a.getInt(2),
                    a.optInt(3, 255));
            } catch (final JSONException e) {
                throw new IllegalArgumentException(e);
            }
        } , value -> {
            final JSONArray a = new JSONArray(value.getRed(), value.getGreen(),
                value.getBlue());
            return value.getAlpha() == 255 ? a : a.put(value.getAlpha());
        });

    public static final Serializer<Rectangle> RECT_SERIALIZER = Serializer
        .of(o -> {
            final JSONArray a = (JSONArray)o;
            try {
                return new Rectangle(a.getInt(0), a.getInt(1), a.getInt(2),
                    a.getInt(3));
            } catch (final JSONException e) {
                throw new IllegalArgumentException(e);
            }
        } , r -> new JSONArray(r.x, r.y, r.width, r.height));

    @SuppressWarnings("unchecked")
    public static <T> Serializer<T[]> getArraySerializer(final Class<T> clazz) {
        return getSerializer(clazz)
            .array(n -> (T[])Array.newInstance(clazz, n));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Stmt> Serializer<T> getStmtSerializer(
        final LogicalSystem logicalSystem)
    {
        final Map<String, Stmt> t = logicalSystem.getStmtTbl();
        return Serializer.of(o -> (T)t.get(o), Stmt::getLabel);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Sym> Serializer<T> getSymSerializer(
        final LogicalSystem logicalSystem)
    {
        final Map<String, Sym> t = logicalSystem.getSymTbl();
        return Serializer.of(o -> (T)t.get(o), Sym::getId);
    }

    @SuppressWarnings("unchecked")
    public static <T> Serializer<T> identity() {
        return Serializer.of(o -> (T)o, v -> v);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Serializer<T> getSerializer(final Class<T> clazz) {
        Serializer s;
        if (Enum.class.isAssignableFrom(clazz))
            s = getEnumSerializer((Class)clazz);
        else if (clazz == Color.class)
            s = COLOR_SERIALIZER;
        else if (clazz.isArray())
            s = getArraySerializer(clazz.getComponentType());
        else
            s = identity();
        return s;
    }

}
