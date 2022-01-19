package maia.topology.node.standard

import maia.configure.Configurable
import maia.configure.ConfigurationElement
import maia.configure.ConfigurationItem
import maia.configure.asReconfigureBlock
import maia.topology.ExecutionState
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.node.base.LockStepTransformer
import maia.util.sign

/**
 * Transformer which outputs a long-integer count of the number of items
 * it has seen (0-based).
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
@Node.WithMetadata("Outputs a long for each item it sees")
class ItemCounter : LockStepTransformer<ItemCounterConfiguration, Any?, Int> {

    @Configurable.Register<ItemCounter, ItemCounterConfiguration>(
        ItemCounter::class,
        ItemCounterConfiguration::class
    )
    constructor(block : ItemCounterConfiguration.() -> Unit = {}) : super(block)

    constructor(config : ItemCounterConfiguration) : this(config.asReconfigureBlock())

    /** The number of items seen so far. */
    private var value by ExecutionState { configuration.start }

    /** Whether the step value is negative. */
    private val negativeStep by lazy { configuration.step < 0 }

    override suspend fun transformSingle(item: Any?): Int {
        // Get the current value to return
        val currentValue = value

        // Check if updating the value will cause over/under-flow
        val willFlow = if (negativeStep)
            Int.MIN_VALUE - configuration.step > currentValue
        else
            Int.MAX_VALUE - configuration.step < currentValue

        // Calculate the next value of the counter
        val newValue = if (!willFlow)
            currentValue + configuration.step
        else if (negativeStep)
            Int.MIN_VALUE
        else
            Int.MAX_VALUE

        // See if we need to stop/reset
        val stopReached = if (negativeStep)
            newValue <= configuration.stop
        else
            newValue >= configuration.stop

        // Perform stop/reset if necessary
        if (!stopReached) {
            value = newValue
        } else if (configuration.resetOnStop) {
            value = configuration.start
        } else {
            stop()
        }

        return currentValue
    }

}


class ItemCounterConfiguration : NodeConfiguration("item counter") {

    @ConfigurationElement.WithMetadata("The value to start counting from")
    var start by ConfigurationItem { 0 }

    @ConfigurationElement.WithMetadata("The amount to increment the counter by for each item")
    var step by ConfigurationItem { 1 }

    @ConfigurationElement.WithMetadata("The value to stop counting at")
    var stop by ConfigurationItem { Int.MAX_VALUE }

    @ConfigurationElement.WithMetadata("Whether to reset the counter once 'stop' is reached and continue counting")
    var resetOnStop by ConfigurationItem { false }

    override fun checkIntegrity() : String? {
        return super.checkIntegrity() ?: if (stop.compareTo(start).sign != step.sign)
            "Sign of step doesn't match start and stop"
        else
            null
    }

}
