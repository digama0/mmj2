//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SessionStore.java  0.01 1/07/2016
 */

package mmj.pa;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;

import org.json.*;

import mmj.pa.BaseSetting.JSONSerializable;

public class SessionStore {

    public static final String KEY_STORE = "store";
    public static final String KEY_OVERRIDE = "manual";
    public static final String KEY_REMOVE = "remove";

    private File file = new File(PaConstants.PROOF_ASST_SETTINGS_FILE_DEFAULT);
    private Supplier<File> path;

    Map<String, JSONSerializable> settings = new TreeMap<>();

    Deque<Serializer<?>> serializers = new ArrayDeque<>();

    public void setMMJ2Path(final Supplier<File> path) {
        this.path = path;
    }

    public File getMMJ2Path() {
        return path.get();
    }

    public void setFile(final File file) {
        this.file = file;
    }

    /**
     * Load the data from the storage file, merging the loaded data with the
     * keys already loaded in memory.
     *
     * @param intoSettings True if keys present in both the file and settings
     *            should take their values from the file, false to take
     *            conflicts from settings.
     * @return The in-memory JSON representation of the merged file
     * @throws IOException If there is an error during the read
     */
    public JSONObject load(final boolean intoSettings) throws IOException {
        return load(intoSettings, (String[])null);
    }

    /**
     * Load the data from the storage file, merging the loaded data with the
     * keys already loaded in memory.
     *
     * @param intoSettings True if keys present in both the file and settings
     *            should take their values from the file, false to take
     *            conflicts from settings.
     * @param settingsToLoad A list of settings to load, or null to load all
     * @return The in-memory JSON representation of the merged file
     * @throws IOException If there is an error during the read
     */
    public JSONObject load(final boolean intoSettings,
        final String... settingsToLoad) throws IOException
    {
        try {
            Object dat = null;
            if (file != null && file.exists() && file.length() != 0)
                try (FileReader r = new FileReader(file)) {
                    dat = new JSONTokener(r).nextValue();
                }
            if (dat == null)
                dat = PaConstants.JSON_INTRODUCTION;
            final JSONObject o = dat instanceof JSONObject ? (JSONObject)dat
                : new JSONObject();
            JSONObject store = o.optJSONObject(KEY_STORE);
            if (store == null)
                o.put(KEY_STORE, store = new JSONObject());
            JSONArray remove = o.optJSONArray(KEY_REMOVE);
            if (remove == null)
                o.put(KEY_REMOVE, remove = new JSONArray());
            for (final Object key : remove)
                store.remove(key);
            remove.clear();
            merge(intoSettings, store, settingsToLoad);
            merge(true, o.optJSONObject(KEY_OVERRIDE), settingsToLoad);
            return o;
        } catch (final JSONException e) {
            throw new IOException(e);
        }
    }

    private void merge(final boolean intoSettings, final JSONObject store,
        final String... settingsToLoad)
    {
        if (store == null)
            return;
        if (settingsToLoad == null)
            for (final Entry<String, JSONSerializable> e : settings.entrySet())
                mergeOne(intoSettings, store, e.getKey(), e.getValue());
        else
            for (final String key : settingsToLoad)
                mergeOne(intoSettings, store, key, settings.get(key));
    }

    private void mergeOne(final boolean intoSettings, final JSONObject store,
        final String key, final JSONSerializable setting)
    {
        if (intoSettings) {
            final Object value = store.opt(key);
            if (value != null)
                try {
                    setting.read(value);
                } catch (final ProofAsstException
                    | IllegalArgumentException e)
                {
                    final MMJException e2 = MMJException.extract(e);
                    new ProofAsstException(e2 == null ? e : e2,
                        PaConstants.ERRMSG_STORE_LOAD_FAIL, key)
                            .printStackTrace();
                }
        }
        else
            store.put(key, setting.write());
    }
    public void addSerializable(final String key, final Consumer<?> read,
        final Supplier<?> write)
    {
        settings.put(key, new JSONSerializable() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean read(final Object o) {
                ((Consumer<Object>)read).accept(o);
                return true;
            }

            @Override
            public Object write() {
                return write.get();
            }
        });
    }

    /**
     * Save the data in memory to the file, overwriting the file completely.
     *
     * @throws IOException If there is an I/O problem
     */
    public void save() throws IOException {
        if (file == null)
            return;
        Files.copy(file.toPath(),
            file.toPath()
                .resolveSibling(file.getName() + PaConstants.BACKUP_SUFFIX),
            StandardCopyOption.REPLACE_EXISTING);
        final String dat = load(false).toString(2);
        try (PrintStream s = new PrintStream(file)) {
            s.println(dat);
        }
    }

    public interface OnChangeListener<T> {
        boolean onChange(T oldValue, T newValue) throws ProofAsstException;
    }

    public <T> Setting<T> addSetting(final String key, final T def)

    {
        return new StoreSetting<>(key, def);
    }

    public <T> Setting<T> addSetting(final String key, final T def,
        final Class<T> clazz)
    {
        return new StoreSetting<>(key, def, clazz);
    }

    public <T> Setting<T> addSetting(final String key, final T def,
        final Serializer<T> serializer)
    {
        return new StoreSetting<>(key, def, serializer);
    }

    public Setting<File> addFileSetting(final String key, final String def)

    {
        return addFileSetting(key, def, path);
    }

    public Setting<File> addFileSetting(final String key, final String def,
        final Supplier<File> path)
    {
        return addFileSetting(key, def, path,
            Serializer.getFileSerializer(path)::deserialize);
    }

    public Setting<File> addFileSetting(final String key, final String def,
        final Supplier<File> path, final Function<String, File> in)

    {
        final NullSetting<String> base = new NullSetting<>(key, def,
            Serializer.identity());
        final Serializer<File> ser = Serializer.getFileSerializer(path);
        return new ExtSetting<String, File>(base,
            s -> s == null ? null : in.apply(s),
            file -> file == null ? null : (String)ser.serialize(file))
        {
            public boolean setStringT(final String s)
                throws ProofAsstException
            {
                return base.setT(s);
            }
        };
    }

    public Setting<Boolean> addSetting(final String key, final boolean def)

    {
        return new StoreSetting<Boolean>(key, def, Serializer.identity()) {
            @Override
            public boolean setString(final String newValue) {
                return ProofAsstPreferences.parseBoolean(newValue);
            }
        };
    }

    public static Setting<Integer> setIntBound(final Setting<Integer> setting,
        final int min, final int max)
    {
        return setting
            .addValidation(
                n -> n >= min && n <= max ? null
                    : new ProofAsstException(
                        PaConstants.ERRMSG_INVALID_INT_RANGE, setting.key(),
                        min, max));
    }

    public static Setting<Integer> setIntBound(final Setting<Integer> setting,
        final IntSupplier min, final IntSupplier max)
    {
        return setting
            .addValidation(
                n -> n >= min.getAsInt() && n <= max.getAsInt() ? null
                    : new ProofAsstException(
                        PaConstants.ERRMSG_INVALID_INT_RANGE, setting.key(),
                        min.getAsInt(), max.getAsInt()));
    }

    public class StoreSetting<T> extends Setting<T> {
        private final String key;
        private final T def;
        private T value;

        /**
         * Create a new setting with the given key and default value.
         *
         * @param key The key for this item in the preferences
         * @param def The default value of the preference
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        private StoreSetting(final String key, final T def)

        {
            this(key, def, def instanceof Enum ? ((Enum)def).getDeclaringClass()
                : (Class)def.getClass());
        }

        /**
         * Create a new setting with the given key and default value.
         *
         * @param key The key for this item in the preferences
         * @param def The default value of the preference
         * @param clazz The class of {@code T}
         */
        private StoreSetting(final String key, final T def,
            final Class<T> clazz)
        {
            this(key, def, Serializer.getSerializer(clazz));
        }

        /**
         * Create a new setting with the given key and default value.
         *
         * @param key The key for this item in the preferences
         * @param def The default value of the preference
         * @param serializer A serializer
         */
        private StoreSetting(final String key, final T def,
            final Serializer<T> serializer)
        {
            super(serializer);
            Objects.requireNonNull(def);
            this.key = key;
            this.def = def;
            settings.put(key, this);
            reset();
        }

        /** @return the current value of this setting. */
        public T get() {
            return value;
        }

        /**
         * Reset this setting to its default value.
         *
         * @return True if the reset was successful
         */
        public boolean reset() {
            return set(def);
        }

        /**
         * @param newValue the new value of this setting.
         * @return true if the setting was accepted
         */
        @Override
        public boolean setRaw(final T newValue) {
            value = newValue;
            return true;
        }

        /** @return the default value of this setting. */
        public T getDefault() {
            return def;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public String toString() {
            return key() + " = " + toJSONString();
        }
    }

    public class ExtSetting<R, T> extends Setting<T> {
        private final Setting<R> base;
        private final Function<R, T> in;
        private final Function<T, R> out;

        /**
         * Create a setting based on the given setting as mapped by the in and
         * out functions.
         *
         * @param base The base setting, of type {@code R}
         * @param in The mapping from type {@code R} to {@code T}
         * @param out The mapping from type {@code T} to {@code R}, presumably
         *            the inverse of {@code in}
         */
        private ExtSetting(final Setting<R> base, final Function<R, T> in,
            final Function<T, R> out)
        {
            super(Serializer.of(in.compose(base.getSerializer()::deserialize),
                ((Function<R, Object>)base.getSerializer()::serialize)
                    .compose(out)));
            this.base = base;
            this.in = in;
            this.out = out;
        }

        @Override
        public T get() {
            return in.apply(base.get());
        }

        @Override
        public T getDefault() {
            return in.apply(base.getDefault());
        }

        @Override
        protected boolean setRaw(final T newValue) {
            try {
                return base.set(out.apply(newValue));
            } catch (final ClassCastException e) {
                throw new IllegalArgumentException(
                    "Error in setting '" + key() + "': value " + newValue
                        + " has type " + newValue.getClass() + ", expected "
                        + getDefault().getClass());
            }
        }

        @Override
        public String key() {
            return base.key();
        }

        @Override
        public String toString() {
            return key() + " = " + toJSONString();
        }
    }

    public class ListSetting<T> extends StoreSetting<List<T>> {

        /**
         * Create a new setting with the given key and null default value.
         *
         * @param key The key for this item in the preferences
         * @param clazz The class of {@code T}
         */
        public ListSetting(final String key, final Class<T> clazz)

        {
            this(key, new ArrayList<>(), clazz);
        }

        /**
         * Create a new setting with the given key and default value.
         *
         * @param key The key for this item in the preferences
         * @param def The default value of the preference
         * @param clazz The class of {@code T}
         */
        public ListSetting(final String key, final List<T> def,
            final Class<T> clazz)
        {
            this(key, def, Serializer.getSerializer(clazz));
        }

        /**
         * Create a new setting with the given key and default value.
         *
         * @param key The key for this item in the preferences
         * @param def The default value of the preference
         * @param serializer A serializer
         */
        public ListSetting(final String key, final List<T> def,
            final Serializer<T> serializer)
        {
            super(key, def, serializer.list());
        }

        /**
         * @param n The index to check
         * @return the current value of this setting.
         */
        public T get(final int n) {
            return get().get(n);
        }

        /**
         * @param n The index to set
         * @param newValue the new value of this setting.
         * @return true if the setting was accepted
         * @throws ProofAsstException If validation fails
         */
        public boolean setT(final int n, final T newValue)
            throws ProofAsstException

        {
            final ArrayList<T> copy = new ArrayList<>(get());
            copy.set(n, newValue);
            return setT(copy);
        }
    }

    public class MapSetting<T> extends StoreSetting<Map<String, T>> {

        /**
         * Create a new setting with the given key and null default value.
         *
         * @param key The key for this item in the preferences
         * @param clazz The class of {@code T}
         */
        public MapSetting(final String key, final Class<T> clazz)

        {
            this(key, new HashMap<>(), clazz);
        }

        /**
         * Create a new setting with the given key and default value.
         *
         * @param key The key for this item in the preferences
         * @param def The default value of the preference
         * @param clazz The class of {@code T}
         */
        public MapSetting(final String key, final Map<String, T> def,
            final Class<T> clazz)
        {
            this(key, def, Serializer.getSerializer(clazz));
        }

        /**
         * Create a new setting with the given key and default value.
         *
         * @param key The key for this item in the preferences
         * @param def The default value of the preference
         * @param serializer A serializer
         */
        public MapSetting(final String key, final Map<String, T> def,
            final Serializer<T> serializer)
        {
            super(key, def, serializer.map());
        }

        /**
         * @param n The index to check
         * @return the current value of this setting.
         */
        public T get(final String n) {
            return get().get(n);
        }

        /**
         * @param n The index to set
         * @param newValue the new value of this setting.
         * @return true if the setting was accepted
         * @throws ProofAsstException If validation fails
         */
        public boolean putT(final String n, final T newValue)
            throws ProofAsstException

        {
            final HashMap<String, T> copy = new HashMap<>(get());
            copy.put(n, newValue);
            return setT(copy);
        }
    }

    public class NullSetting<T> extends ExtSetting<Optional<T>, T> {
        public NullSetting(final String key, final Serializer<T> serializer)

        {
            this(key, null, serializer);
        }

        public NullSetting(final String key, final T def,
            final Serializer<T> serializer)
        {
            super(
                new StoreSetting<>(key, Optional.ofNullable(def),
                    serializer.opt()),
                o -> o.orElse(null), Optional::ofNullable);
        }
    }
}
