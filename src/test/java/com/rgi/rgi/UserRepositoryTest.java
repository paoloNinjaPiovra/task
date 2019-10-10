package com.rgi.rgi;

import com.rgi.rgi.entity.Task;
import com.rgi.rgi.entity.User;
import com.rgi.rgi.repository.TaskRepository;
import com.rgi.rgi.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Before
    public void setUp() {
        User user = new User("user");
        user.setCode("u5be48d5-ae7c-4816-a210-9c984cf760a0");
        User user1 = new User("user1");
        user1.setCode("u5be48d5-ae7c-4816-a210-9c984cf760a1");
        User user2 = new User("user2");
        user2.setCode("u5be48d5-ae7c-4816-a210-9c984cf760a2");
        User user3 = new User("user3");
        user3.setCode("u5be48d5-ae7c-4816-a210-9c984cf760a3");
        User user4 = new User("user4");
        user4.setCode("u5be48d5-ae7c-4816-a210-9c984cf760a4");
        User user5 = new User("user5");
        user5.setCode("u5be48d5-ae7c-4816-a210-9c984cf760a5");

        Set<User> userSet = new HashSet<>();
        userSet.add(user);
        Set<User> userSet1 = new HashSet<>();
        userSet1.add(user);
        userSet1.add(user1);
        Set<User> userSet2 = new HashSet<>();
        userSet2.add(user);
        userSet2.add(user1);
        userSet2.add(user2);
        Set<User> userSet3 = new HashSet<>();
        userSet3.add(user);
        userSet3.add(user1);
        userSet3.add(user2);
        userSet3.add(user3);
        Set<User> userSet4 = new HashSet<>();
        userSet4.add(user);
        userSet4.add(user1);
        userSet4.add(user2);
        userSet4.add(user3);
        userSet4.add(user4);
        Set<User> userSet5 = new HashSet<>();
        userSet5.add(user);
        userSet5.add(user1);
        userSet5.add(user2);
        userSet5.add(user3);
        userSet5.add(user4);
        userSet5.add(user5);

        Task task = new Task("task 0", "task 0 description");
        task.setCode("t5be48d5-ae7c-4816-a210-9c984cf760a0");
        for (User currentUser : userSet)
            userRepository.save(currentUser);
        task.setUsers(userSet);
        taskRepository.save(task);

        Task task1 = new Task("task 1 ", "task 1 description");
        task1.setCode("t5be48d5-ae7c-4816-a210-9c984cf760a1");
        for (User currentUser : userSet1)
            userRepository.save(currentUser);
        task1.setUsers(userSet1);
        taskRepository.save(task1);

        Task task2 = new Task("task 2", "task 2 description");
        task2.setCode("t5be48d5-ae7c-4816-a210-9c984cf760a2");
        for (User currentUser : userSet2)
            userRepository.save(currentUser);
        task2.setUsers(userSet2);
        taskRepository.save(task2);

        Task task3 = new Task("task 3", "task 3 description");
        task3.setCode("t5be48d5-ae7c-4816-a210-9c984cf760a3");
        for (User currentUser : userSet3)
            userRepository.save(currentUser);
        task3.setUsers(userSet3);
        taskRepository.save(task3);

        Task task4 = new Task("task 4", "task 4 description");
        task4.setCode("t5be48d5-ae7c-4816-a210-9c984cf760a4");
        for (User currentUser : userSet4)
            userRepository.save(currentUser);
        task4.setUsers(userSet4);
        taskRepository.save(task4);

        Task task5 = new Task("task 5", "task 5 description");
        task5.setCode("t5be48d5-ae7c-4816-a210-9c984cf760a5");
        for (User currentUser : userSet5)
            userRepository.save(currentUser);
        task5.setUsers(userSet5);
        taskRepository.save(task5);
    }

    @Test
    @Transactional
    public void findUserByNameTest() {
        User user = userRepository.findUserByName("user");
        Assert.assertTrue(user != null);
        Assert.assertTrue(user.getName().equals("user"));
        Assert.assertTrue(user.getCode().equals("u5be48d5-ae7c-4816-a210-9c984cf760a0"));
    }

    @Test
    @Transactional
    public void findUserByNameTestKo() {
        User user = userRepository.findUserByName("fakeUser");
        Assert.assertTrue(user == null);
    }

    @Test
    @Transactional
    public void findUserByUserCodeTest() {
        User user = userRepository.findUserByUserCode("u5be48d5-ae7c-4816-a210-9c984cf760a0");
        Assert.assertTrue(user != null);
    }

    @Test
    @Transactional
    public void findUserByUserCodeTestKo() {
        User user = userRepository.findUserByUserCode("fakeUser");
        Assert.assertTrue(user == null);
    }

    @Test
    @Transactional
    public void findUserByTaskCodeTest() {
        List<User> userList = userRepository.findUserByTaskCode ("t5be48d5-ae7c-4816-a210-9c984cf760a0","u5be48d5-ae7c-4816-a210-9c984cf760a0");
        Assert.assertTrue(userList.size() == 1);
        Assert.assertTrue(userList.get(0).getCode().equals("u5be48d5-ae7c-4816-a210-9c984cf760a0"));
        Assert.assertTrue(userList.get(0).getName().equals("user"));
    }

    @Test
    @Transactional
    public void findUserByTaskCodeTestKoForTaskCode() {
        List<User> userList = userRepository.findUserByTaskCode ("t5be48d5-ae7c-4816-a210-9c984cf760aX","u5be48d5-ae7c-4816-a210-9c984cf760a0");
        Assert.assertTrue(userList.isEmpty());

    }

    @Test
    @Transactional
    public void findUserByTaskCodeTestKoforUserCode() {
        List<User> userList = userRepository.findUserByTaskCode ("t5be48d5-ae7c-4816-a210-9c984cf760a0","u5be48d5-ae7c-4816-a210-9c984cf760aX");
        Assert.assertTrue(userList.isEmpty());
    }
}