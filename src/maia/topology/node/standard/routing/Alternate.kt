package maia.topology.node.standard.routing

import maia.configure.Configurable
import maia.configure.ConfigurationElement
import maia.configure.ConfigurationItem
import maia.configure.asReconfigureBlock
import maia.topology.ExecutionState
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.io.Input
import maia.topology.io.Outputs
import maia.topology.io.Throughput
import maia.topology.io.util.allClosed
import maia.topology.node.base.ContinuousLoopNode
import maia.topology.node.base.WithPrimaryInput


/**
 * Cycles the input items over a number of outputs.
 */
@Node.WithMetadata("Cycles the input items over a number of outputs")
class Alternate<T> : ContinuousLoopNode<AlternateConfiguration>, WithPrimaryInput<T> {

    @Configurable.Register<Alternate<*>, AlternateConfiguration>(Alternate::class, AlternateConfiguration::class)
    constructor(block : AlternateConfiguration.() -> Unit = {}) : super(block)

    constructor(config : AlternateConfiguration) : this(config.asReconfigureBlock())

    @Throughput.WithMetadata("The input of items")
    override val primaryInput by Input<T>()

    @Throughput.WithMetadata("The cycled outputs")
    val outputs by Outputs<T>(configuration.numOutputs)

    /** The next output to push an item to. */
    var nextOutput by ExecutionState { configuration.start }

    override fun loopCondition() : Boolean = !outputs.allClosed

    override suspend fun mainLoopInner() {
        val item = primaryInput.pullOrAbort()
        var pushed = false
        while (!pushed && !outputs.allClosed) {
            pushed = outputs[nextOutput].push(item)
            nextOutput = (nextOutput + 1) % outputs.size
            if (!configuration.skipClosed) break
        }
    }

}

class AlternateConfiguration : NodeConfiguration("alternate") {

    @ConfigurationElement.WithMetadata("The number of outputs to cycle over")
    var numOutputs by ConfigurationItem { 2 }

    @ConfigurationElement.WithMetadata("The output to start from")
    var start by ConfigurationItem { 0 }

    @ConfigurationElement.WithMetadata("Whether to skip closed outputs")
    var skipClosed by ConfigurationItem { false }

    override fun checkIntegrity() : String? {
        return super.checkIntegrity() ?:
                checkNumOutputs() ?:
                checkStart()
    }

    private fun checkNumOutputs() : String? {
        return if (numOutputs < 0)
            "numOutputs can't be negative (got $numOutputs)"
        else
            null
    }

    private fun checkStart() : String? {
        return when {
            numOutputs == 0 -> null
            start !in 0 until numOutputs -> "start ($start) is not in [0, $numOutputs)"
            else -> null
        }
    }

}
