package mmj.util;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class InternPool<T> {
    private final WeakHashMap<T, WeakReference<T>> pool = new WeakHashMap<T, WeakReference<T>>();

    public synchronized T intern(final T object) {
        T res = null;
        // (The loop is needed to deal with race
        // conditions where the GC runs while we are
        // accessing the 'pool' map or the 'ref' object.)
        do {
            WeakReference<T> ref = pool.get(object);
            if (ref == null) {
                ref = new WeakReference<T>(object);
                pool.put(object, ref);
                res = object;
            }
            else
                res = ref.get();
        } while (res == null);
        return res;
    }
}
