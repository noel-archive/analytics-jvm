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

package org.noelware.analytics.jvm.server.internal.metadata;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.noelware.analytics.jvm.server.ServerMetadata;
import org.noelware.analytics.protobufs.v1.BuildFlavour;

public class DefaultServerMetadata implements ServerMetadata {
    private final Map<String, Object> metadata = new HashMap<>();

    private final String BUILD_FLAVOUR_KEY = "build:flavour";
    private final String COMMIT_HASH_KEY = "commit:hash";
    private final String BUILD_DATE_KEY = "build:date";
    private final String PRODUCT_KEY = "product";
    private final String VERSION_KEY = "version";
    private final String VENDOR_KEY = "vendor";

    /**
     * Sets the {@link BuildFlavour} metadata field.
     * @param flavour {@link BuildFlavour} to set
     */
    @Override
    public void setDistributionType(@NotNull BuildFlavour flavour) {
        metadata.put(BUILD_FLAVOUR_KEY, flavour);
    }

    /**
     * Sets the build date metadata field.
     * @param date {@link Instant} of when the product was built in
     */
    @Override
    public void setBuildDate(@NotNull Instant date) {
        metadata.put(BUILD_DATE_KEY, date.toString());
    }

    /**
     * Sets the build date metadata field.
     *
     * @param date {@link String} in the ISO-8601 format of when the product was built in
     */
    @Override
    public void setBuildDate(@NotNull String date) {
        metadata.put(BUILD_DATE_KEY, date);
    }

    /**
     * Sets the commit hash metadata field
     * @param commitHash commit hash string
     */
    @Override
    public void setCommitHash(@NotNull String commitHash) {
        metadata.put(COMMIT_HASH_KEY, commitHash);
    }

    /**
     * Sets the product name
     * @param productName product name
     */
    @Override
    public void setProductName(@NotNull String productName) {
        metadata.put(PRODUCT_KEY, productName);
    }

    /**
     * Sets the product's version
     * @param version version
     */
    @Override
    public void setVersion(@NotNull String version) {
        metadata.put(VERSION_KEY, version);
    }

    /**
     * Sets the product's vendor
     * @param vendor vendor name
     */
    @Override
    public void setVendor(@NotNull String vendor) {
        metadata.put(VENDOR_KEY, vendor);
    }

    /**
     * Returns the distribution type for this server.
     */
    @Override
    public @NotNull BuildFlavour distributionType() {
        return (BuildFlavour) metadata.getOrDefault(BUILD_FLAVOUR_KEY, BuildFlavour.UNRECOGNIZED);
    }

    /**
     * Returns the build date (as a ISO8601 formatted date) on when the product that
     * supports the Noelware Analytics Protocol was built. This can return <code>null</code>
     * if the product doesn't support it.
     */
    @Override
    public @Nullable String buildDate() {
        final Object attr = metadata.getOrDefault(BUILD_DATE_KEY, null);
        if (attr == null) return null;

        return (String) attr;
    }

    /**
     * Returns a version control source-based commit hash of the given product, long or short.
     * This can return <code>null</code> if the product is not open sourced, or it doesn't support it.
     */
    @Override
    public @Nullable String commitHash() {
        final Object attr = metadata.getOrDefault(COMMIT_HASH_KEY, null);
        if (attr == null) return null;

        return (String) attr;
    }

    /**
     * Returns the version of this product.
     */
    @Override
    public @NotNull String version() {
        return (String) metadata.getOrDefault(VERSION_KEY, "unknown");
    }

    /**
     * Returns the product name
     */
    @Override
    public @NotNull String product() {
        return (String) metadata.getOrDefault(PRODUCT_KEY, "unknown");
    }

    /**
     * Returns the product's vendor.
     */
    @Override
    public @NotNull String vendor() {
        return (String) metadata.getOrDefault(VENDOR_KEY, "unknown");
    }
}
