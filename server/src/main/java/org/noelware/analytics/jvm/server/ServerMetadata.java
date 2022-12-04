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

package org.noelware.analytics.jvm.server;

import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.noelware.analytics.protobufs.v1.BuildFlavour;

/**
 * Represents a collection of metadata keys that are available to the {@link AnalyticsServer}. This collection
 * just defines what it is needed for the server to recognize what keys need to be used in the <code>ReceiveStats</code>
 * method.
 */
public interface ServerMetadata {
    /**
     * Sets the {@link BuildFlavour} metadata field.
     * @param flavour {@link BuildFlavour} to set
     */
    void setDistributionType(@NotNull BuildFlavour flavour);

    /**
     * Sets the build date metadata field.
     * @param date {@link Instant} of when the product was built in
     */
    void setBuildDate(@NotNull Instant date);

    /**
     * Sets the build date metadata field.
     * @param date {@link String} in the ISO-8601 format of when the product was built in
     */
    void setBuildDate(@NotNull String date);

    /**
     * Sets the commit hash metadata field
     * @param commitHash commit hash string
     */
    void setCommitHash(@NotNull String commitHash);

    /**
     * Sets the product name
     * @param productName product name
     */
    void setProductName(@NotNull String productName);

    /**
     * Sets the product's version
     * @param version version
     */
    void setVersion(@NotNull String version);

    /**
     * Sets the product's vendor
     * @param vendor vendor name
     */
    void setVendor(@NotNull String vendor);

    /**
     * Returns the distribution type for this server.
     */
    @NotNull
    BuildFlavour distributionType();

    /**
     * Returns the build date (as a ISO8601 formatted date) on when the product that
     * supports the Noelware Analytics Protocol was built. This can return <code>null</code>
     * if the product doesn't support it.
     */
    @Nullable
    String buildDate();

    /**
     * Returns a version control source-based commit hash of the given product, long or short.
     * This can return <code>null</code> if the product is not open sourced, or it doesn't support it.
     */
    @Nullable
    String commitHash();

    /**
     * Returns the version of this product.
     */
    @NotNull
    String version();

    /**
     * Returns the product name
     */
    @NotNull
    String product();

    /**
     * Returns the product's vendor.
     */
    @NotNull
    String vendor();
}
