package maia.topology.node.standard.routing

import maia.configure.Configurable
import maia.configure.asReconfigureBlock
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.io.Input
import maia.topology.io.Output
import maia.topology.io.Throughput
import maia.topology.io.util.allClosed
import maia.topology.io.util.multiPush
import maia.topology.node.base.ContinuousLoopNode
import maia.topology.node.base.WithPrimaryInput


/**
 * Splits elements of a pair over two separate outputs.
 */
@Node.WithMetadata("Splits elements of a pair over two separate outputs")
class Split<A, B> : ContinuousLoopNode<NodeConfiguration>, WithPrimaryInput<Pair<A, B>> {

    @Configurable.Register<Split<*, *>, NodeConfiguration>(
        Split::class,
        NodeConfiguration::class
    )
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : NodeConfiguration) : this(config.asReconfigureBlock())

    @Throughput.WithMetadata("The input of pairs")
    override val primaryInput by Input<Pair<A, B>>()

    @Throughput.WithMetadata("The output of the first item in the pair (second if swapped)")
    val first by Output<A>()

    @Throughput.WithMetadata("The output of the second item in the pair (first if swapped)")
    val second by Output<B>()

    /** The pair of outputs to push the split items to. */
    private val outputs by lazy { Pair(first, second) }

    override fun loopCondition() : Boolean = !outputs.allClosed

    override suspend fun mainLoopInner() {
        outputs.multiPush(primaryInput.pullOrAbort())
    }

}
