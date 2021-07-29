package māia.topology.node.standard.test

import kotlinx.coroutines.delay
import māia.configure.Configurable
import māia.configure.ConfigurationElement
import māia.configure.ConfigurationItem
import māia.configure.asReconfigureBlock
import māia.configure.util.ifNotAbsent
import māia.topology.ExecutionState
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.node.base.Source
import java.lang.System.currentTimeMillis

/**
 * A source of long-integer values that outputs a new value at a specified rate.
 */
@Node.WithMetadata("A source of long-integer values that outputs a new value at a specified rate.")
class DummyHotSource : Source<DummyHotSourceConfiguration, Long> {

    @Configurable.Register<DummyHotSource, DummyHotSourceConfiguration>(
        DummyHotSource::class,
        DummyHotSourceConfiguration::class
    )
    constructor(block : DummyHotSourceConfiguration.() -> Unit = {}) : super(block)

    constructor(config : DummyHotSourceConfiguration) : this(config.asReconfigureBlock())

    /** The time-stamp at which to output the next item. */
    private var nextOutputTime by ExecutionState(::currentTimeMillis)

    /** The next item to output. */
    private var nextOutputValue by ExecutionState { 0L }

    override suspend fun produce(): Long {
        ifNotAbsent { configuration.intervalMillis } then {
            // Get the current time
            val now = currentTimeMillis()

            // If we are too early to output an item, sleep until it's time
            if (nextOutputTime > now) delay(nextOutputTime - now)

            // Calculate the next next output time
            nextOutputTime += it
        }

        return nextOutputValue++
    }
}

open class DummyHotSourceConfiguration : NodeConfiguration("dummyHotSource") {

    @ConfigurationElement.WithMetadata("The number of milliseconds to wait between emitting each long value")
    var intervalMillis by ConfigurationItem<Long>(true)

    override fun checkIntegrity() : String? {
        return super.checkIntegrity() ?:
                checkIntervalMillis()
    }

    private fun checkIntervalMillis() : String? {
        return ifNotAbsent { intervalMillis } then {
            if (it < 0)
                "intervalMillis can't be negative (got $intervalMillis)"
            else
                null
        } otherwise {
            null
        }
    }


}
