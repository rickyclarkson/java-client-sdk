package uk.org.netvu.util;

import javax.swing.SwingUtilities;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Timer;
import java.util.TimerTask;

public class EDTMonitor {
    public static void start() {
        new Timer("EDTMonitor", true).scheduleAtFixedRate(new TimerTask() {
            volatile int running = 0;
            @Override
            public void run() {
                if (running >= 1)
                    threadDump();
                running++;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        running--;
                    }
                });
            }
        }, 30000, 10000);
    }

    public static void threadDump() {
        System.err.println("Beginning of thread dump.");
        final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        for (ThreadInfo info: bean.dumpAllThreads(bean.isObjectMonitorUsageSupported(), bean.isSynchronizerUsageSupported()))
            System.err.println(info);
        System.err.println("End of thread dump.");
    }
}
