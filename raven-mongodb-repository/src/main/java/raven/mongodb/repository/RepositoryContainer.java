package raven.mongodb.repository;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 *
 */
public class RepositoryContainer {

    private static HashMap<String, Object> instances;

    static {
        instances = new HashMap<>();
    }

    static void clear() {
        instances.clear();
    }

    /**
     * @param service
     * @param <T>
     */
    public synchronized static <T extends MongoBaseRepository> void register(final T service) {
        Class<?> clazz = service.getClass();
        String k = getKey(clazz);

        instances.putIfAbsent(k, service);
    }

    /**
     * @param clazz
     * @param service
     * @param <T>
     */
    public synchronized static <T extends MongoBaseRepository> void register(final Class<T> clazz, final Object service) {
        String k = getKey(clazz);

        instances.putIfAbsent(k, service);
    }

    /**
     * @param clazz
     * @param func
     * @param <T>
     */
    public synchronized static <T extends MongoBaseRepository> void register(final Class<T> clazz, final Supplier<T> func) {
        String k = getKey(clazz);
        instances.putIfAbsent(k, func.get());
    }

    /**
     * @param packageName
     */
    public synchronized static void registerAll(final String packageName) {

        List<Class> clazzList = Util.getAllClassByInterface(MongoBaseRepository.class, packageName);
        MongoBaseRepository repos;
        for (Class clazz : clazzList) {
            repos = null;
            try {
                Method method = clazz.getDeclaredMethod(Util.CREATE_INSTANCE_METHOD, null);
                method.setAccessible(true);
                repos = (MongoBaseRepository) method.invoke(null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (repos == null) {
                try {
                    repos = (MongoBaseRepository) clazz.newInstance();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (repos != null) {
                register(clazz, repos);
            }
        }

    }

    /**
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
