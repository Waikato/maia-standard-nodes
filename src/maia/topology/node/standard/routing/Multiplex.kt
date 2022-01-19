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
 * Receives items on one input, and forwards them to an output selected via
 * another input.
 */
@Node.WithMetadata("Outputs received items on a selected output")
class Multiplex<T> : ContinuousLoopNode<MultiplexConfiguration>, WithPrimaryInput<T> {

    @Configurable.Register<Multiplex<*>, MultiplexConfiguration>(
        Multiplex::class,
        MultiplexConfiguration::class
    )
    constructor(block : MultiplexConfiguration.() -> Unit = {}) : super(block)

    constructor(config : MultiplexConfiguration) : this(config.asReconfigureBlock())

    @Throughput.WithMetadata("The input of (item, output) pairs")
    override val primaryInput by Input<T>()

    @Throughput.WithMetadata("Selects the output to forward the next item")
    val selectInput by Input<Int>()

    @Throughput.WithMetadata("The multiplexed outputs")
    val outputs by Outputs<T>(configuration.size)

    /** The next output to push on. */
    private var nextOutput by ExecutionState { 0 }

    /** List of both inputs for selection. */
    private val inputs by lazy { listOf(primaryInput, selectInput) }

    /** Makes the select input required if we should close when it closes. */
    private val requiredInputs by lazy {
        if (configuration.closeOnSelectClose)
            setOf(selectInput)
        else
            setOf()
    }

    override fun loopCondition() : Boolean {
        return !outputs.allClosed && (!configuration.closeOnSelectClose || !selectInput.isClosed )
    }

    override suspend fun mainLoopInner() {
        inputs.selectOrAbort(requiredInputs) { input, value ->
            if (input === primaryInput) {
                if (!selectionOutOfRange(nextOutput)) outputs[nextOutput].push(value as T)
            } else {
                val selection = value as Int
                if (configuration.errorOnSelectOutOfRange && selectionOutOfRange(selection)) {
                    throw SelectOutOfRange(this, selection)
                }
                nextOutput = selection
            }
        }
    }

    /**
     * Checks if the selected output is out-of-range.
     *
     * @param selection     The selected output.
     * @return              Whether the selected output is out-of-range.
     */
    private fun selectionOutOfRange(selection : Int) : Boolean {
        return selection !in 0 until outputs.size
    }

    /**
     * Error for when the selected output is out-of-range.
     *
     * @param node          The [Multiplex] node raising the error.
     * @param selection     The selected output that is in error.
     */
    class SelectOutOfRange(node : Multiplex<*>, selection : Int) :
            Exception(
                    "${node.selectInput.name} received selection $selection which is out-of-range " +
                            "(number of outputs = ${node.outputs.size})"
            )

}


class MultiplexConfiguration : NodeConfiguration("multiplex") {

    @ConfigurationElement.WithMetadata("The number of multiplexed outputs")
    var size by ConfigurationItem { 2 }

    @ConfigurationElement.WithMetadata("Whether to stop this node if the selectInput closes")
    var closeOnSelectClose by ConfigurationItem { false }

    @ConfigurationElement.WithMetadata(
            "Whether to throw an exception if the selected output is out-of-range " +
                    "(otherwise the items are silently discarded)"
    )
    var errorOnSelectOutOfRange by ConfigurationItem { true }

    override fun checkIntegrity() : String? {
        return super.checkIntegrity() ?:
                checkSize()
    }

    private fun checkSize() : String? {
        return if (size < 0)
            "size can't be negative (got $size)"
        else
            null
    }

}
