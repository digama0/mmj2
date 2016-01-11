package mmj.pa;

import java.util.function.Predicate;
import java.util.function.Supplier;

import org.json.*;

public interface BaseSetting<T> extends Supplier<T>, Predicate<T> {

    /** @return the current value of this setting. */
    T get();

    /**
     * @param newValue the new value of this setting.
     * @return true if the setting was accepted
     */
    boolean set(final T newValue);

    /** @return the default value of this setting. */
    T getDefault();

    /**
     * Reset this setting to its default value.
     *
     * @return True if the reset was successful
     */
    default boolean reset() {
        return set(getDefault());
    }

    /**
     * @return true if the setting is set to its default value
     */
    default boolean isDefault() {
        return get().equals(getDefault());
    }

    default boolean test(final T t) {
        return set(t);
    }

    public interface JSONSerializable {

        /**
         * Read the current value of this setting from the session store.
         *
         * @param o The JSON stored value
         * @return True if the read was successful
         */
        public boolean read(final Object o);

        /**
         * Write the value of this setting into the session store.
         *
         * @return A JSON value suitable for insertion
         */
        public Object write();
    }

    public static interface JSONSetting<T>
        extends BaseSetting<T>, JSONSerializable, JSONString
    {

        /** @return This setting's serializer */
        Serializer<T> getSerializer();

        /**
         * @return The serialized version of this setting
         */
        default Object getSerial() {
            return getSerializer().serialize(get());
        }

        /**
         * @param newValue the new value of this setting.
         * @return true if the setting was accepted
         */
        default boolean setSerial(final Object newValue) {
            return set(getSerializer().deserialize(newValue));
        }

        /**
         * @param newValue the new value of this setting.
         * @return true if the setting was accepted
         */
        default boolean setString(final String newValue) {
            return setSerial(new JSONTokener(newValue).nextValue());
        }

        default boolean read(final Object o) {
            return o == null || setSerial(o);
        }

        default Object write() throws JSONException {
            return isDefault() ? null : getSerial();
        }

        default String toJSONString() {
            try {
                return JSONObject.valueToString(getSerial());
            } catch (final JSONException e) {
                return get().toString();
            }
        }
    }
}
