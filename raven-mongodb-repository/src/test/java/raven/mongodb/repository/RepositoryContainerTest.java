package raven.mongodb.repository;

import org.junit.Assert;
import org.junit.Test;

public class RepositoryContainerTest {

    @Test
    public void Register() throws Exception {

        UserRepositoryImpl repos = new UserRepositoryImpl();
        RepositoryContainer.register(repos);
        UserRepositoryImpl repos2 = RepositoryContainer.resolve(UserRepositoryImpl.class);

        Assert.assertEquals(repos, repos2);
    }


}
