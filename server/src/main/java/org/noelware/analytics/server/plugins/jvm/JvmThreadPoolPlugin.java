/*
 * 🌂 analytics-jvm: Client and server implementation of Noelware Analytics in Java, supported for both Java and Kotlin
 * Copyright (c) 2022 Noelware
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

package org.noelware.analytics.server.plugins.jvm;

import com.google.protobuf.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.noelware.analytics.server.Pair;
import org.noelware.analytics.server.plugins.Plugin;

public class JvmThreadPoolPlugin implements Plugin {
  private final ThreadMXBean threadMXBean;

  public JvmThreadPoolPlugin() {
    this(ManagementFactory.getThreadMXBean());
  }

  public JvmThreadPoolPlugin(ThreadMXBean bean) {
    this.threadMXBean = bean;
  }

  @Override
  public Pair<String, Map<String, Value>> getStatistics() {
    var map = new HashMap<String, Value>();
    var threadIds = getThreadIdsValue();
    var count = getThreadCountValue();
    var threadState = getThreadStateValue();

    map.put("ids", threadIds);
    map.put("count", count);
    map.put("state", threadState);

    return new Pair<>("threads", Collections.unmodifiableMap(map));
  }

  private Value getThreadIdsValue() {
    var ids =
        Arrays.stream(threadMXBean.getAllThreadIds())
            .mapToObj(
                data -> {
                  var value = Value.newBuilder();
                  value.setNumberValue(data);

                  return value.build();
                })
            .toList();

    return Value.newBuilder()
        .setListValue(ListValue.newBuilder().addAllValues(ids).build())
        .build();
  }

  private Value getThreadCountValue() {
    var threadCount = threadMXBean.getThreadCount();
    var daemonCount = threadMXBean.getDaemonThreadCount();
    var struct = Struct.newBuilder();
    var map = struct.getFieldsMap();

    map.put("count", Value.newBuilder().setNumberValue(threadCount).build());
    map.put("daemon", Value.newBuilder().setNumberValue(daemonCount).build());

    return Value.newBuilder().setStructValue(struct.build()).build();
  }

  private Value getThreadStateValue() {
    var threadStateMap = new HashMap<String, Integer>();
    for (var threadId : threadMXBean.getAllThreadIds()) {
      var info = threadMXBean.getThreadInfo(threadId);
      if (!threadStateMap.containsKey(info.getThreadState().name())) {
        threadStateMap.putIfAbsent(info.getThreadState().name(), 0);
      }

      var count = threadStateMap.get(info.getThreadState().name()).longValue() + 1;
      threadStateMap.put(info.getThreadState().name(), ((Long) count).intValue());
    }

    var struct = Struct.newBuilder();
    var map = struct.getFieldsMap();
    var newMap = new HashMap<String, Value>();
    for (Map.Entry<String, Integer> entry : threadStateMap.entrySet()) {
      newMap.put(entry.getKey(), Value.newBuilder().setNumberValue(entry.getValue()).build());
    }

    map.putAll(newMap);
    return Value.newBuilder().setStructValue(struct.build()).build();
  }
}
