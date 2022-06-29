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

package org.noelware.analytics.client.data;

import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;
import java.time.Instant;
import java.util.*;
import org.noelware.analytics.protobufs.v1.BuildFlavour;

public class RetrieveStatsResponse {
  private Map<String, Object> data;
  private BuildFlavour buildFlavour;
  private Instant snapshotDate;
  private String buildDate;
  private String commitSha;
  private String product;
  private String version;

  private Map<String, Object> iterateOverStructMap(Struct structMap) {
    var data = new HashMap<String, Object>();
    for (Map.Entry<String, Value> iter : structMap.getFieldsMap().entrySet()) {
      var key = iter.getKey();
      var value = getStructItem(iter.getValue());

      data.put(key, value);
    }

    return data;
  }

  private List<Object> iterateOverList(ListValue listValue) {
    var list = listValue.getValuesList();
    var data = new ArrayList<>();

    for (Value entry : list) {
      data.add(getStructItem(entry));
    }

    return data;
  }

  private Object getStructItem(Value value) {
    if (value.hasBoolValue()) {
      return value.getBoolValue();
    } else if (value.hasStringValue()) {
      return value.getStringValue();
    } else if (value.hasNumberValue()) {
      return value.getNumberValue();
    } else if (value.hasNullValue()) {
      return null;
    } else if (value.hasStructValue()) {
      return iterateOverStructMap(value.getStructValue());
    } else if (value.hasListValue()) {
      return iterateOverList(value.getListValue());
    } else {
      throw new IllegalStateException("Unknown value was specified.");
    }
  }

  public BuildFlavour getBuildFlavour() {
    return buildFlavour;
  }

  public void setBuildFlavour(BuildFlavour flavour) {
    this.buildFlavour = flavour;
  }

  public void setBuildFlavour(int type) {
    this.buildFlavour = BuildFlavour.forNumber(type);
  }

  public Instant getSnapshotDate() {
    return snapshotDate;
  }

  public void setSnapshotDate(Instant date) {
    this.snapshotDate = date;
  }

  public void setSnapshotDate(Timestamp timestamp) {
    this.snapshotDate = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Struct data) {
    this.data = Collections.unmodifiableMap(iterateOverStructMap(data));
  }

  public String getBuildDate() {
    return buildDate;
  }

  public void setBuildDate(String buildDate) {
    this.buildDate = buildDate;
  }

  public String getCommitSha() {
    return commitSha;
  }

  public void setCommitSha(String commitSha) {
    this.commitSha = commitSha;
  }

  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
