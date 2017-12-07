package raven.mongodb.repository;

import java.util.concurrent.ConcurrentHashMap;

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
    public static <T extends MongoBaseRepository> void register(Class<T> clazz, Func func) {
        String k = getKey(clazz);
        instances.putIfAbsent(k, func.create());
    }

    /**
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends MongoBaseRepository> T resolve(Class<T> clazz)
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
    private static <T> String getKey(Class<T> clazz) {
        return clazz.getName();
    }

    public interface Func {
        <T extends MongoBaseRepository> T create();
    }

}
