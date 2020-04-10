package com.motus.example.javautilscadence.workflow;

import java.util.Date;

public class GreatActivitiesImpl implements GreatActivities {
    @Override
    public Date getADateForSomeReason() {
        return new Date();
    }

    @Override
    public void printSomethingToConsole(String something) {
        System.out.println("yay!  printing something:"  + something);
    }
}
