package com.example.cta_map;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.List;

class Thread2 implements Runnable
{
    private final List<Integer> taskQueue;

    public Thread2(List<Integer> sharedQueue)
    {
        this.taskQueue = sharedQueue;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                consume();
            } catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void consume() throws InterruptedException
    {
        synchronized (taskQueue)
        {
            while (taskQueue.isEmpty())
            {
                System.out.println("Queue is empty " + Thread.currentThread().getName() + " is waiting , size: " + taskQueue.size());
                taskQueue.wait();
            }
            Thread.sleep(1000);
            int i = (Integer) taskQueue.remove(0);
            System.out.println("Consumed: " + i);
            taskQueue.notifyAll();
        }
    }
}
