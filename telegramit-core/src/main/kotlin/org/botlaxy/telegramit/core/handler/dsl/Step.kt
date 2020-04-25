package org.botlaxy.telegramit.core.handler.dsl

class Step<T : Any>(
    val key: String,
    val entry: EntryBlock,
    val validation: ValidationBlock?,
    val resolver: ResolverBlock<T>?,
    val next: NextStepBlock
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Step<*>

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}
