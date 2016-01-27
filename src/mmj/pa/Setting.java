package mmj.pa;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import mmj.pa.BaseSetting.JSONSetting;
import mmj.pa.SessionStore.OnChangeListener;

public abstract class Setting<T> implements JSONSetting<T> {
    private final List<OnChangeListener<T>> listeners = new ArrayList<>();
    private final Serializer<T> serializer;

    public Setting(final Serializer<T> serializer) {
        this.serializer = serializer;
    }

    @Override
    public Serializer<T> getSerializer() {
        return serializer;
    }

    public Setting<T> addListener(final OnChangeListener<T> listener) {
        listeners.add(listener);
        return this;
    }

    public Setting<T> addListener(final OnChangeListener<T> listener,
        final boolean refresh)
    {
        addListener(listener);
        if (refresh)
            listener.onChange(get(), get());
        return this;
    }

    public Setting<T> addValidation(final Function<T, String> validator) {
        return addListener((o, v) -> {
            final String errMsg = validator.apply(v);
            if (errMsg == null)
                return true;
            throw new IllegalArgumentException(errMsg);
        });
    }

    /**
     * @param newValue the new value of this setting.
     * @return true if the setting was accepted
     */
    public boolean set(final T newValue) {
        final T oldValue = get();
        if (newValue == null)
            return false;
        if (newValue.equals(oldValue))
            return true;
        for (final OnChangeListener<T> listener : listeners)
            if (!listener.onChange(oldValue, newValue))
                return false;
        return setRaw(newValue);
    }

    protected abstract boolean setRaw(final T newValue);

    public abstract String key();
}
