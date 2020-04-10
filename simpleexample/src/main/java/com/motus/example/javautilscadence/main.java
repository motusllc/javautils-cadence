package com.motus.example.javautilscadence;

import com.motus.example.javautilscadence.workflow.WorkerStarter;

public class main {
    public static void main (String [] args) {
        WorkerStarter workerStarter = new WorkerStarter();
        workerStarter.run();
        System.out.println("<<<<<<<<<< Running, press Ctrl+C to quit >>>>>>>>>>");
        System.console().readLine();
    }
}
