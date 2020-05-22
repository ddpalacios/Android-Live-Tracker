package com.example.cta_map;

public class Time {

    public Integer get_estimated_time_arrival(int speed, Double distance){

        return (int) ((distance / speed)*100);
    }
}
