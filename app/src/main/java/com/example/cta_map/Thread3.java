package com.example.cta_map;


public class Thread3 implements Runnable {
    final Message message;

    public Thread3(Message message){
        this.message = message;

    }


    @Override
    public void run() {
        int i=0;
                while (true) {
                    synchronized (this.message) {
                        this.message.notifyAll();
                }

        }



    }
}
