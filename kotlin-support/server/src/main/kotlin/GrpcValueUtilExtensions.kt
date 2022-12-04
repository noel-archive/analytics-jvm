package org.noelware.analytics.jvm.server.extensions

import com.google.protobuf.Value
import org.noelware.analytics.jvm.server.util.GrpcValueUtil

/**
 * Transforms this string into a [Value]. This internally uses the [GrpcValueUtil.toValue] method.
 * @return [serializable gRPC value][Value]
 */
fun String.toGrpcValue(): Value = GrpcValueUtil.toValue(this)

/**
 * Transforms this number into a [Value]. This internally uses the [GrpcValueUtil.toValue] method.
 * @return [serializable gRPC value][Value]
 */
fun Number.toGrpcValue(): Value = GrpcValueUtil.toValue(this)

/**
 * Transforms this boolean into a [Value]. This internally uses the [GrpcValueUtil.toValue] method.
 * @return [serializable gRPC value][Value]
 */
fun Boolean.toGrpcValue(): Value = GrpcValueUtil.toValue(this)

/**
 * Transforms this list into a [Value]. This internally uses the [GrpcValueUtil.toValue] method.
 * @return [serializable gRPC value][Value]
 */
fun <T> List<T>.toGrpcValue(): Value = GrpcValueUtil.toValue(this)
