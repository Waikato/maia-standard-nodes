package maia.topology.node.standard.routing

import maia.configure.Configurable
import maia.configure.ConfigurationElement
import maia.configure.ConfigurationItem
import maia.configure.asReconfigureBlock
import maia.topology.ExecutionState
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.node.base.LockStepTransformer


/**
 * Only lets through a certain number of items before finishing.
 */
@Node.WithMetadata("Only lets through a certain number of items before finishing")
class LetPassForRange<T> : LockStepTransformer<LetPassForRangeConfiguration, T, T> {

    @Configurable.Register<LetPassForRange<*>, LetPassForRangeConfiguration>(
        LetPassForRange::class,
        LetPassForRangeConfiguration::class
    )
    constructor(block : LetPassForRangeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : LetPassForRangeConfiguration) : this(config.asReconfigureBlock())

    /** The number of items that have been seen so far. */
    private var count by ExecutionState { 0L }

    /** The range specified by the configuration. */
    private val range by lazy { configuration.start..configuration.endInclusive}

    override suspend fun transformSingle(item : T) : T {
        try {
            if ((count in range) xor configuration.invert) {
                return item
            } else {
                continueNodeLoop()
            }
        } finally {
            if (count == Long.MAX_VALUE) {
                if (configuration.errorOnOverflow)
                    throw IllegalStateException("Counter overflow")
            } else {
                count++
                if (configuration.closeAfter && !configuration.invert && count > configuration.endInclusive)
                    stop()
            }
        }
    }

}

class LetPassForRangeConfiguration : NodeConfiguration("first") {

    @ConfigurationElement.WithMetadata("The inclusive enumeration of the first item to pass")
    var start by ConfigurationItem { 0L }

    @ConfigurationElement.WithMetadata("The inclusive enumeration of the last item to pass")
    var endInclusive by ConfigurationItem { 0L }

    @ConfigurationElement.WithMetadata("Whether to instead pass items outside the range")
    var invert by ConfigurationItem { false }

    @ConfigurationElement.WithMetadata("Whether to throw an exception if the counter overflows")
    var errorOnOverflow by ConfigurationItem { true }

    @ConfigurationElement.WithMetadata("Whether to close the node once the range is exhausted")
    var closeAfter by ConfigurationItem { true }

}
