/*
 * 🐻‍❄️🌂 analytics-jvm: Client and server implementation of Noelware Analytics in Java, supported for both Java and Kotlin
 * Copyright (c) 2022 Noelware <team@noelware.org>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.noelware.analytics.jvm.server.extensions.jvm;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.noelware.analytics.jvm.server.extensions.Extension;

public class JvmThreadsExtension implements Extension<JvmThreadsExtension.JvmThreadsData> {
    private final ThreadMXBean threadMXBean;

    public JvmThreadsExtension() {
        this(ManagementFactory.getThreadMXBean());
    }

    public JvmThreadsExtension(ThreadMXBean bean) {
        threadMXBean = bean;
    }

    /**
     * Returns the name of this {@link Extension} to be used in the final result when
     * sending out this extension's data.
     */
    @Override
    public String name() {
        return "threads";
    }

    /**
     * This method is called to supply the data that is available to be ingested to the Analytics Server
     * or any other third-party you allow.
     */
    @Override
    public JvmThreadsData supply() {
        final long[] ids = threadMXBean.getAllThreadIds();
        final java.lang.management.ThreadInfo[] threads = threadMXBean.getThreadInfo(ids, 0);

        final ArrayList<ThreadInfo> infos = new ArrayList<>();
        for (java.lang.management.ThreadInfo info : threads) {
            final ThreadInfo threadInfo = new ThreadInfo(
                    info.getThreadId(),
                    info.getThreadName(),
                    info.getThreadState().name().toUpperCase(Locale.ROOT),
                    info.isSuspended(),
                    info.isInNative(),
                    info.isDaemon(),
                    info.getPriority(),
                    Arrays.asList(info.getStackTrace()));

            infos.add(threadInfo);
        }

        final long[] deadlock = threadMXBean.findDeadlockedThreads();
        return new JvmThreadsData(
                threadMXBean.getThreadCount(),
                threadMXBean.getDaemonThreadCount(),
                threadMXBean.getPeakThreadCount(),
                deadlock != null ? deadlock.length : -1,
                infos);
    }

    /**
     * Represents the data that the Analytics Server can ingest for JVM thread-related data.
     * @param current    how many current threads are there in this JVM
     * @param background how many background threads are there in this JVM
     * @param peak       how many peak threads are there in this JVM
     * @param deadlocked how many deadlocked threads are there in this JVM
     * @param info       specific thread information
     */
    public record JvmThreadsData(int current, int background, int peak, int deadlocked, List<ThreadInfo> info) {}

    /**
     * Represents a single thread's information.
     *
     * @param id             thread id
     * @param name           thread name
     * @param state          thread state
     * @param suspended      is the thread in a suspended state?
     * @param inNative       was the thread created natively
     * @param isDaemonThread is the thread a background one
     * @param priority       priority of the thread
     * @param stacktrace     thread stacktrace
     */
    public record ThreadInfo(
            long id,
            String name,
            String state,
            boolean suspended,
            boolean inNative,
            boolean isDaemonThread,
            int priority,
            List<StackTraceElement> stacktrace) {}
}
