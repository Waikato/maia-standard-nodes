package maia.topology.node.standard

import kotlinx.coroutines.delay
import maia.configure.Configurable
import maia.configure.ConfigurationElement
import maia.configure.ConfigurationItem
import maia.configure.asReconfigureBlock
import maia.configure.util.ifNotAbsent
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.node.base.LockStepTransformer

/**
 * Transformer which passes-through the item after a configurable amount of time.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
@Node.WithMetadata("Transformer which passes-through the item after a configurable amount of time")
class Delay<T> : LockStepTransformer<DelayConfiguration, T, T> {

    @Configurable.Register<Delay<*>, DelayConfiguration>(Delay::class, DelayConfiguration::class)
    constructor(block : DelayConfiguration.() -> Unit = {}) : super(block)

    constructor(config : DelayConfiguration) : super(config.asReconfigureBlock())

    override suspend fun transformSingle(item: T): T {
        // If the delay is configured, enact it
        ifNotAbsent { configuration.timeMillis } then { delay(it) }

        return item
    }

}

class DelayConfiguration : NodeConfiguration("delay") {

    @ConfigurationElement.WithMetadata("The amount of delay to have")
    var timeMillis by ConfigurationItem<Long>(true)

    override fun checkIntegrity() : String? {
        return super.checkIntegrity() ?:
                checkTimeMillis()
    }

    private fun checkTimeMillis() : String? {
        return ifNotAbsent { timeMillis } then {
            if (it < 0)
                "Delay can't be negative"
            else
                null
        } otherwise {
            null
        }

    }

}
