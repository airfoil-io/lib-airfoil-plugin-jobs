package io.airfoil.plugins.jobs

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID
import kotlin.runCatching

@JvmInline
@Serializable(with = JobIdSerializer::class)
value class JobId(val value: UUID) {
    override fun toString() = value.toString()

    companion object {
        fun random() = JobId(UUID.randomUUID())

        fun fromString(string: String) = JobId(UUID.fromString(string))

        operator fun invoke(string: String): JobId? = runCatching {
            JobId.fromString(string)
        }.getOrNull()
    }

}

object JobIdSerializer : KSerializer<JobId> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.airfoil.plugins.jobs.JobIdSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): JobId =
        JobId(UUID.fromString(decoder.decodeString()))

    override fun serialize(encoder: Encoder, value: JobId) {
        encoder.encodeString(value.value.toString())
    }
}
