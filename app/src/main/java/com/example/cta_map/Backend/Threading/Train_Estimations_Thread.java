//package com.example.cta_map.Backend.Threading;
//
//import android.content.Context;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//
//import androidx.annotation.RequiresApi;
//
//import com.example.cta_map.DataBase.CTA_DataBase;
//import com.example.cta_map.Displayers.Chicago_Transits;
//import com.example.cta_map.Displayers.Time;
//import com.example.cta_map.Displayers.UserLocation;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Objects;
//import java.util.TreeMap;
//
//public class Train_Estimations_Thread implements Runnable {
//    final Message msg;
//    android.os.Handler handler;
//    Context context;
//
//public Train_Estimations_Thread(Context context, Message msg, android.os.Handler handler) {
//    this.msg = msg;
//    this.context = context;
//    this.handler = handler;
//
//}
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    public void run() {
//
//        CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
//        int table_records_removed = cta_dataBase.retrieve_and_delete_all_records("all_trains_table");
//
//
//            while (this.msg.IsSending()){
//                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
//                synchronized (this.msg) {
//                    try {
//                    int record_removed = cta_dataBase.retrieve_and_delete_all_records("all_trains_table");
//
//                    ArrayList<AllTrainsTable> incoming_trains = this.msg.getChosen_trains();
//                     if (incoming_trains == null) {
//                         send_to_UI("all_chosen_trains", null);
//                            try {
//                                Thread.sleep(10000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            this.msg.notifyAll();
//                        } else {
//                            for (AllTrainsTable new_train : incoming_trains) {
//                                cta_dataBase.CommitRecordToAllTrainsTable(new_train);
//                            }
//                            ArrayList<Object> committed_incoming_trains = cta_dataBase.excecuteQuery("*", "all_trains_table", null, "to_target_eta");
//                            send_to_UI("all_chosen_trains", committed_incoming_trains);
//                            try {
//                                Thread.sleep(10000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            this.msg.notifyAll();
//                        }
//                    }catch (Exception e){e.printStackTrace();}
//                    }
//                }
//            }
//            public void send_to_UI(String key, ArrayList<Object> message){
//                Bundle bundle = new Bundle();
//                android.os.Message handler_msg = this.handler.obtainMessage();
//                bundle.putSerializable(key, message);
//                handler_msg.setData(bundle);
//                handler.sendMessage(handler_msg);
//
//            }
//
//        }
//
