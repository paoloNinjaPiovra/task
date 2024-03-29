package com.task.task.service;

import com.task.task.entity.Task;
import com.task.task.entity.User;
import com.task.task.enums.Status;
import com.task.task.exception.TaskFoundException;
import com.task.task.exception.TaskNotFoundException;
import com.task.task.exception.UserNotFoundException;
import com.task.task.model.Task4List;
import com.task.task.model.TaskForm;
import com.task.task.model.UserForm;
import com.task.task.repository.TaskRepository;
import com.task.task.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = {UserNotFoundException.class}, propagation = Propagation.REQUIRES_NEW, readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Task4List> list(String userSession) throws UserNotFoundException {
        log.info("taskService: list begin... ");
        if (StringUtils.isNotEmpty(userSession)) {
            List<Task> task = taskRepository.findTask(userSession);
            log.info("taskService: list ... end!");
            return task.stream().map(t -> new Task4List(t.getName(), t.getStatus(), t.getCode())).collect(Collectors.toList());
        } else {
            log.info("taskService: list ...end - no user found!");
            throw new UserNotFoundException(userSession);
        }
    }

    @Override
    @Transactional(rollbackFor = {UserNotFoundException.class, TaskNotFoundException.class}, propagation = Propagation.REQUIRES_NEW, readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Task get(String userSession, String taskCode) throws UserNotFoundException, TaskNotFoundException {
        log.info("taskService: get begin... ");
        if (StringUtils.isNotEmpty(userSession)) {
            Task task = getTask(userSession, taskCode, "get");
            log.info("taskService: get ... end!");
            return task;
        } else {
            log.info("taskService: get ...end - no user found!");
            throw new UserNotFoundException(userSession);
        }
    }

    @Override
    @Transactional(rollbackFor = {UserNotFoundException.class, TaskNotFoundException.class}, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void delete(String userSession, String taskCode) throws UserNotFoundException, TaskNotFoundException {
        log.info("taskService: delete begin... ");
        if (StringUtils.isNotEmpty(userSession)) {
            Task task = getTask(userSession, taskCode, "delete");
            taskRepository.delete(task);
            log.info("taskService: delete ... end!");
        } else {
            log.info("taskService: delete ...end - no user found!");
            throw new UserNotFoundException(userSession);
        }
    }

    @Override
    @Transactional(rollbackFor = {UserNotFoundException.class, TaskNotFoundException.class}, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public Task patch(String userSession, TaskForm newTask, String taskCode) throws UserNotFoundException, TaskNotFoundException {
        log.info("taskService: patch begin... ");
        if (StringUtils.isNotEmpty(userSession)) {
            Task task = getTask(userSession, taskCode, "patch");
            if (null != task) {
                return patchTask(task, newTask, "patch");
            } else {
                log.info("taskService: patch ...end - no task found!");
                throw new TaskNotFoundException(taskCode);
            }
        } else {
            log.info("taskService: patch ...end - no user found!");
            throw new UserNotFoundException(userSession);
        }
    }

    @Override
    @Transactional(rollbackFor = {UserNotFoundException.class, TaskNotFoundException.class}, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public Task save(String userSession, TaskForm newTask) throws UserNotFoundException, TaskFoundException {
        log.info("taskService: save begin... ");
        if (StringUtils.isNotEmpty(userSession)) {
            if (null != taskRepository.findTaskByCode(newTask.getCode())) {
                log.info("taskService: save ...end - no task found!");
                throw new TaskFoundException(newTask.getCode());
            }
            return saveTask(newTask, "save");
        } else {
            log.info("taskService: save ...end - no user found!");
            throw new UserNotFoundException(userSession);
        }
    }

    @Override
    @Transactional(rollbackFor = {UserNotFoundException.class, TaskNotFoundException.class}, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public Task close(String userSession, String taskCode) throws UserNotFoundException, TaskNotFoundException {
        log.info("taskService: close begin... ");
        if (StringUtils.isNotEmpty(userSession)) {
            Task task = getTask(userSession, taskCode, "close");
            if (null != task) {
                task.setStatus(Status.CLOSED);
                log.info("taskService: close ... end!");
                return taskRepository.save(task);
            } else {
                log.info("taskService: close ...end - no task found!");
                throw new TaskNotFoundException(taskCode);
            }
        } else {
            log.info("taskService: close ...end - no user found!");
            throw new UserNotFoundException(userSession);
        }
    }

    private Task getTask(String userSession, String taskCode, String methodName) throws TaskNotFoundException {
        Task task = taskRepository.findTaskByCodeAndUserCode(taskCode, userSession);
        if (null == task) {
            log.info("taskService: " + methodName + " ...end - no task (user: " + userSession + " - task: " + taskCode + ") found!");
            throw new TaskNotFoundException(userSession);
        }
        log.info("taskService: " + methodName + " : " + task.getCode() + " : " + task.getName() + " : " + task.getDescription());
        return task;
    }

    private Task saveTask(TaskForm newTask, String method) {
        Task task = new Task(newTask.getName(), newTask.getDescription());
        User user;
        Set<User> associatedUsers = new HashSet<>();
        for (UserForm currentUser : newTask.getUsers()) {
            user = userRepository.findUserByName(currentUser.getName());
            if (null == user) {
                user = userRepository.save(new User(currentUser.getName()));
            }
            associatedUsers.add(user);
        }
        task.setUsers(associatedUsers);
        log.info("taskService: " + method + " ... end!");
        return taskRepository.save(task);
    }

    private Task patchTask(Task task, TaskForm newTask, String method) throws TaskNotFoundException {
        if (null != task) {
            User user;
            Set<User> newUsers = new HashSet<>();
            if (!task.getDescription().equals(newTask.getDescription()))
                task.setDescription(newTask.getDescription());
            if (!task.getName().equals(newTask.getName()))
                task.setName(newTask.getName());
            if (!task.getStatus().equals(newTask.getStatus()))
                task.setStatus(newTask.getStatus());
            for (UserForm currentUser : newTask.getUsers()) {
                user = userRepository.findUserByUserCode(currentUser.getCode());
                if (null == user) {
                    user = userRepository.save(new User(currentUser.getName(), currentUser.getCode()));
                }
                newUsers.add(user);
            }
            task.setUsers(newUsers);
            log.info("taskService: " + method + " ... end!");
            return taskRepository.save(task);
        }
        log.info("taskService: close ...end - no task found!");
        throw new TaskNotFoundException(newTask.getCode());
    }
}
