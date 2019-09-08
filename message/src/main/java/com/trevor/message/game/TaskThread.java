package com.trevor.message.game;

public class TaskThread implements Runnable{

    public Task task;

    public TaskThread(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        task.execut();
    }
}
