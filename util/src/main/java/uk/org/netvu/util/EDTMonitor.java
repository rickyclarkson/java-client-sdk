package uk.org.netvu.util;

import javax.swing.SwingUtilities;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
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
        }, 50000, 20000);
    }

    public static void threadDump() {
        System.err.println("Beginning of thread dump.");
        final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        for (ThreadInfo info: bean.dumpAllThreads(bean.isObjectMonitorUsageSupported(), bean.isSynchronizerUsageSupported()))
            System.err.println(toString(info));
        System.err.println("End of thread dump.");
    }

    // A copy of ThreadInfo.toString() but without the hardcoded limit of 8 in the stack traces.
    public static String toString(ThreadInfo info) {
        StringBuilder sb = new StringBuilder("\"" + info.getThreadName() + "\"" +
                                             " Id=" + info.getThreadId() + " " +
                                             info.getThreadState());
        if (info.getLockName() != null) {
            sb.append(" on " + info.getLockName());
        }
        if (info.getLockOwnerName() != null) {
            sb.append(" owned by \"" + info.getLockOwnerName() +
                      "\" Id=" + info.getLockOwnerId());
        }
        if (info.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (info.isInNative()) {
            sb.append(" (in native)");
        }
        sb.append('\n');
        int i = 0;
        for (; i < info.getStackTrace().length; i++) {
            StackTraceElement ste = info.getStackTrace()[i];
            sb.append("\tat " + ste.toString());
            sb.append('\n');
            if (i == 0 && info.getLockInfo() != null) {
                Thread.State ts = info.getThreadState();
                switch (ts) {
                    case BLOCKED:
                        sb.append("\t-  blocked on " + info.getLockInfo());
                        sb.append('\n');
                        break;
                    case WAITING:
                        sb.append("\t-  waiting on " + info.getLockInfo());
                        sb.append('\n');
                        break;
                    case TIMED_WAITING:
                        sb.append("\t-  waiting on " + info.getLockInfo());
                        sb.append('\n');
                        break;
                    default:
                }
            }

            for (MonitorInfo mi : info.getLockedMonitors()) {
                if (mi.getLockedStackDepth() == i) {
                    sb.append("\t-  locked " + mi);
                    sb.append('\n');
                }
            }
       }
       if (i < info.getStackTrace().length) {
           sb.append("\t...");
           sb.append('\n');
       }

       LockInfo[] locks = info.getLockedSynchronizers();
       if (locks.length > 0) {
           sb.append("\n\tNumber of locked synchronizers = " + locks.length);
           sb.append('\n');
           for (LockInfo li : locks) {
               sb.append("\t- " + li);
               sb.append('\n');
           }
       }
       sb.append('\n');
       return sb.toString();
    }
}
