package raven.mongodb.repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 *
 */
public class RepositoryContainer {

    private static ConcurrentHashMap<String, Object> instances;

    static {
        instances = new ConcurrentHashMap<>();
    }

    /**
     * @param service
     * @param <T>
     */
    public static <T extends MongoBaseRepository> void register(T service) {
        Class<?> clazz = service.getClass();
        String k = getKey(clazz);

        instances.putIfAbsent(k, service);
    }

    /**
     * @param clazz
     * @param func
     * @param <T>
     */
    public static <T extends MongoBaseRepository> void register(final Class<T> clazz, final Supplier<T> func) {
        String k = getKey(clazz);
        instances.putIfAbsent(k, func.get());
    }

    /**
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T extends MongoBaseRepository> T resolve(final Class<T> clazz)
            throws Exception {
        String k = getKey(clazz);
        T repos = (T) instances.get(k);
        if (repos == null) {
            throw new Exception("this repository(" + k + ") is not register");
        }
        return repos;
    }

    /**
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> String getKey(final Class<T> clazz) {
        return clazz.getName();
    }

}
