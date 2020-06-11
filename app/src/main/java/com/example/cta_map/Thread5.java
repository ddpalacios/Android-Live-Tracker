package com.example.cta_map;

public class Thread5 implements Runnable {
    Message msg;


    public Thread5(Message msg){
        this.msg = msg;
    }


    public void run(){
        synchronized (this.msg) {
            while (this.msg.IsSending()) {

                this.msg.notify();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }



    }
}