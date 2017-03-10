package org.ir.tool.benchmark;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekamolid on 12/5/2016.
 */
public class Timer {
    private List<TimerItem> timerItems = new ArrayList<>();
    private TimerItem lastTimerItem;

    public void start(String name) {
        if (lastTimerItem != null) {
            throw new IllegalStateException("stop previous one");
        }
        lastTimerItem = new TimerItem(name);
    }

    public void start(String name, boolean print) {
        if (print) {
            System.out.println(name + " started");
        }
        start(name);
    }

    public void stop() {
        assert lastTimerItem != null;
        lastTimerItem.runTime = System.currentTimeMillis() - lastTimerItem.startTime;
        timerItems.add(lastTimerItem);
        lastTimerItem = null;
    }

    private class TimerItem {
        String name;
        long runTime;
        long startTime;

        private TimerItem(String name) {
            this.name = name;
            startTime = System.currentTimeMillis();
        }
    }

    public void print() {
        int i = 1;
        for (TimerItem timerItem : timerItems) {
            System.out.println(String.format("%d) %s %d", i++, fillSpace(timerItem.name, 25), timerItem.runTime));
        }
    }

    private static String fillSpace(String str, int totalLength) {
        StringBuilder stringBuffer = new StringBuilder(str);
        for (int i = 0; i < totalLength - str.length(); i++) {
            stringBuffer.append(" ");
        }
        return stringBuffer.toString();
    }
}
