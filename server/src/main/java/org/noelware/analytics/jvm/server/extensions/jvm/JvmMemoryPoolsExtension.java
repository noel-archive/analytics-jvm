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

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import org.noelware.analytics.jvm.server.extensions.Extension;
import org.noelware.analytics.jvm.server.serialization.Serializable;
import org.noelware.analytics.jvm.server.util.GrpcValueUtil;

public class JvmMemoryPoolsExtension implements Extension<JvmMemoryPoolsExtension.MemoryPools> {
    private final MemoryMXBean memoryMXBean;

    public JvmMemoryPoolsExtension() {
        this(ManagementFactory.getMemoryMXBean());
    }

    public JvmMemoryPoolsExtension(MemoryMXBean bean) {
        memoryMXBean = bean;
    }

    /**
     * Returns the name of this {@link Extension} to be used in the final result when
     * sending out this extension's data.
     */
    @Override
    public String name() {
        return "memory_pool";
    }

    /**
     * This method is called to supply the data that is available to be ingested to the Analytics Server
     * or any other third-party you allow.
     */
    @Override
    public MemoryPools supply() {
        return new MemoryPools(
                new JvmMemoryUsage(memoryMXBean.getNonHeapMemoryUsage()),
                new JvmMemoryUsage(memoryMXBean.getHeapMemoryUsage()));
    }

    public record MemoryPools(JvmMemoryUsage nonHeap, JvmMemoryUsage heap) implements Serializable {
        @Override
        public Value toGrpcValue() {
            final Struct.Builder struct = Struct.newBuilder();
            struct.putFields("heap", GrpcValueUtil.toValue(heap));
            struct.putFields("non_heap", GrpcValueUtil.toValue(nonHeap));

            return GrpcValueUtil.toValue(struct.build());
        }
    }

    public record JvmMemoryUsage(long init, long used, long committed, long max) implements Serializable {
        public JvmMemoryUsage(MemoryUsage usage) {
            this(usage.getInit(), usage.getUsed(), usage.getCommitted(), usage.getMax());
        }

        @Override
        public Value toGrpcValue() {
            final Struct.Builder struct = Struct.newBuilder();
            struct.putFields("init", GrpcValueUtil.toValue(init));
            struct.putFields("used", GrpcValueUtil.toValue(used));
            struct.putFields("committed", GrpcValueUtil.toValue(committed));
            struct.putFields("max", GrpcValueUtil.toValue(max));

            return GrpcValueUtil.toValue(struct.build());
        }
    }
}
