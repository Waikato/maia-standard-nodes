package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.ConfigurationElement
import māia.configure.ConfigurationItem
import māia.configure.asReconfigureBlock
import māia.topology.ExecutionState
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.io.Output
import māia.topology.io.Throughput
import māia.topology.node.base.LockStepTransformer

/*
 * TODO
 *
 * TODO: Add option to make pushing to 'tick' asynchronous.
 */

/**
 * Outputs a 'tick' for every N items that pass through it.
 */
@Node.WithMetadata("Outputs a tick on a secondary output for every N items passed")
class Ticker<T> : LockStepTransformer<TickerConfiguration, T, T> {

    @Configurable.Register<Ticker<*>, TickerConfiguration>(Ticker::class, TickerConfiguration::class)
    constructor(block : TickerConfiguration.() -> Unit = {}) : super(block)

    constructor(config : TickerConfiguration) : super(config.asReconfigureBlock())

    @Throughput.WithMetadata("The output that ticks get pushed to")
    val tick by Output<Tick>()

    /** The number of items seen so far, modulo configuration.every. */
    private var count by ExecutionState { 0 }

    override fun loopCondition() : Boolean {
        return if (primaryOutput.isClosed) {
            if (tick.isClosed)
                false
            else
                !configuration.closeWithPrimaryOutput
        } else if (tick.isClosed) {
            !configuration.closeWithTickOutput
        } else {
            true
        }
    }

    override suspend fun transformSingle(item: T): T {

        if (!tick.isClosed) {
            // Update counter
            count = (count + 1) % configuration.every

            // Output a tick on wrap-around
            if (count == 0) {
                tick.push(Tick)
            }
        }

        return item
    }

    /** The 'tick' object that gets pushed every N items. */
    object Tick
}

class TickerConfiguration : NodeConfiguration("ticker") {

    @ConfigurationElement.WithMetadata("How often to output ticks")
    var every by ConfigurationItem { 1 }

    @ConfigurationElement.WithMetadata(
            "Whether to stop this node when the 'tick' output closes"
    )
    var closeWithTickOutput by ConfigurationItem { false }

    @ConfigurationElement.WithMetadata(
            "Whether to stop this node when the primary output closes"
    )
    var closeWithPrimaryOutput by ConfigurationItem { false }

    override fun checkIntegrity() : String? {
        return super.checkIntegrity()
                ?: checkEvery()
    }

    private fun checkEvery() : String? {
        return if (every < 1)
            "every can't be zero or negative, got $every"
        else
            null
    }

}
