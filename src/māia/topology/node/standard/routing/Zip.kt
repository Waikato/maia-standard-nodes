package māia.topology.node.standard.routing

import māia.configure.Configurable
import māia.configure.asReconfigureBlock
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.io.Input
import māia.topology.io.Output
import māia.topology.io.Throughput
import māia.topology.node.base.ContinuousLoopNode
import māia.topology.node.base.WithPrimaryOutput


/**
 * Zips 2 inputs into a single output of pairs.
 */
@Node.WithMetadata("Zips 2 inputs into a single output of pairs")
class Zip<A, B> : ContinuousLoopNode<NodeConfiguration>, WithPrimaryOutput<Pair<A, B>> {

    @Configurable.Register<Zip<*, *>, NodeConfiguration>(
        Zip::class,
        NodeConfiguration::class
    )
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : NodeConfiguration) : this(config.asReconfigureBlock())

    @Throughput.WithMetadata("The input of the first element of the pair")
    val inputA by Input<A>()

    @Throughput.WithMetadata("The input of the second element of the pair")
    val inputB by Input<B>()

    @Throughput.WithMetadata("The output of pairs")
    override val primaryOutput by Output<Pair<A, B>>()

    /** The inputs group for selection. */
    private val inputs by lazy { listOf(inputA, inputB) }

    override fun loopCondition() : Boolean = !primaryOutput.isClosed

    override suspend fun mainLoopInner() {
        inputs.selectOrAbort { input, value ->
            primaryOutput.push(
                    if (input === inputA)
                        Pair(value as A, inputB.pullOrAbort())
                    else
                        Pair(inputA.pullOrAbort(), value as B)
            )
        }
    }
}
