package com.example.cta_map.Displayers;

public class Time {

    public Integer get_estimated_time_arrival(Integer speed, Double distance){

        return (int) ((distance / speed)*100);
    }

}
