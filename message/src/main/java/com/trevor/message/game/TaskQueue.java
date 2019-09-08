package com.trevor.message.game;

import com.google.common.collect.Maps;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class TaskQueue implements ApplicationRunner {

    /**
     * 每個房間的任務隊列
     */
    public static Map<String ,ConcurrentLinkedQueue<Task>> taskQueueMap = Maps.newConcurrentMap();

    /**
     * executor1管的房间id
     */
    public static List<String> executor1ToRoomIds= new CopyOnWriteArrayList<>();

    /**
     * executor2管的房间id
     */
    public static List<String> executor2ToRoomIds= new CopyOnWriteArrayList<>();

    private static Executor executor1 = Executors.newFixedThreadPool(1);

    private static Executor executor2 = Executors.newFixedThreadPool(1);

    /**
     * 添加队列
     * @param roomId
     */
    public void addQueue(String roomId){
        taskQueueMap.put(roomId ,new ConcurrentLinkedQueue<>());
        int executor1ToRoomIdsSize = executor1ToRoomIds.size();
        int executor2ToRoomIdsSize = executor2ToRoomIds.size();
        if (executor1ToRoomIdsSize > executor2ToRoomIdsSize) {
            executor2ToRoomIds.add(roomId);
        }else if (executor1ToRoomIdsSize < executor2ToRoomIdsSize) {
            executor1ToRoomIds.add(roomId);
        }else {
            executor2ToRoomIds.add(roomId);
        }
    }

    /**
     * 删除队列
     * @param roomId
     * @return
     */
    public Boolean deleteQueue(String roomId){
        if (!taskQueueMap.get(roomId).isEmpty()) {
            return Boolean.FALSE;
        }
        taskQueueMap.remove(roomId);
        if (executor1ToRoomIds.contains(roomId)) {
            executor1ToRoomIds.remove(roomId);
        }else if (executor2ToRoomIds.contains(roomId)) {
            executor2ToRoomIds.remove(roomId);
        }
        return Boolean.TRUE;
    }

    /**
     * 添加任务
     * @param roomId
     * @param task
     */
    public void addTask(String roomId ,Task task) {
        ConcurrentLinkedQueue<Task> taskQueue = taskQueueMap.get(roomId);
        if (taskQueue != null) {
            taskQueue.offer(task);
        }
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true) {
            Iterator<ConcurrentLinkedQueue<Task>> iterator = taskQueueMap.values().iterator();
            while (iterator.hasNext()) {
                ConcurrentLinkedQueue<Task> taskQueue = iterator.next();
                Task poll = taskQueue.poll();
                if (poll != null && executor1ToRoomIds.contains(poll.getRoomId())) {
                    executor1.execute(new TaskThread(poll));
                }
                if (poll != null && executor2ToRoomIds.contains(poll.getRoomId())) {
                    executor2.execute(new TaskThread(poll));
                }
            }
        }
    }
}
