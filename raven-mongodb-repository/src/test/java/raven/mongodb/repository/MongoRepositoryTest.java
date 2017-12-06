package raven.mongodb.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class MongoRepositoryTest {

    int size = 10;

    //@After
    @Before
    public void init() {

        MongoBaseRepository<User> repos = new UserRepositoryImpl();
        repos.getDatabase().drop();
    }

    @Test
    public void insert() throws Exception {

        User user = new User();
        String uuid = UUID.randomUUID().toString();
        user.setName(uuid);
        user.setAge(123);

        MongoRepository<User, Long> repos = new UserRepositoryImpl();
        repos.insert(user);

        Assert.assertNotEquals(user.getId(), 0);

        user = repos.get(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getName(), uuid);
    }

    @Test
    public void insertBatch() throws Exception {

        ArrayList<User> list = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            User user = new User();
            user.setName(UUID.randomUUID().toString());
            user.setAge(i * 10);
            list.add(user);
        }

        MongoRepository<User, Long> repos = new UserRepositoryImpl();
        repos.insertBatch(list);

        for (User user : list) {
            Assert.assertNotEquals(user.getId(), 0);
        }

        long count = repos.count(null);
        Assert.assertEquals(count, size);

    }
}
