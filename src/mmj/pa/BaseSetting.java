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
     * @throws ProofAsstException If validation failed
     */
    boolean setT(final T newValue) throws ProofAsstException;

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

    default boolean set(final T newValue) {
        try {
            return setT(newValue);
        } catch (final ProofAsstException e) {
            throw new IllegalArgumentException(e);
        }
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
         * @throws ProofAsstException If validation failed
         */
        public boolean read(final Object o) throws ProofAsstException;

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
         * @throws ProofAsstException If validation failed
         */
        default boolean setSerialT(final Object newValue)
            throws ProofAsstException
        {
            return setT(getSerializer().deserialize(newValue));
        }

        /**
         * @param newValue the new value of this setting.
         * @return true if the setting was accepted
         * @throws IllegalArgumentException If validation failed
         */
        default boolean setSerial(final Object newValue) {
            try {
                return setSerialT(newValue);
            } catch (final ProofAsstException e) {
                throw new IllegalArgumentException(e);
            }
        }

        /**
         * @param newValue the new value of this setting.
         * @return true if the setting was accepted
         * @throws ProofAsstException If validation failed
         */
        default boolean setStringT(final String newValue)
            throws ProofAsstException
        {
            return setSerialT(new JSONTokener(newValue).nextValue());
        }

        /**
         * @param newValue the new value of this setting.
         * @return true if the setting was accepted
         * @throws IllegalArgumentException If validation failed
         */
        default boolean setString(final String newValue) {
            try {
                return setStringT(newValue);
            } catch (final ProofAsstException e) {
                throw new IllegalArgumentException(e);
            }
        }

        default boolean read(final Object o) throws ProofAsstException {
            return o == null || setSerialT(o);
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
