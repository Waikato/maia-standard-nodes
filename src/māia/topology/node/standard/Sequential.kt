package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.asReconfigureBlock
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.io.Input
import māia.topology.io.Output
import māia.topology.io.Throughput
import māia.topology.io.util.allClosed
import māia.topology.node.base.ContinuousLoopNode
import māia.topology.node.base.WithPrimaryInput

/**
 * Forwards the input on two outputs, making sure the first output is received
 * before the second is sent.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
@Node.WithMetadata(
        "Forwards the input on two outputs, making sure the first output is received " +
        "before the second is sent."
)
class Sequential<T>: ContinuousLoopNode<NodeConfiguration>, WithPrimaryInput<T> {

    @Configurable.Register<Sequential<*>, NodeConfiguration>(Sequential::class, NodeConfiguration::class)
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(configuration : NodeConfiguration) : this(configuration.asReconfigureBlock())

    @Throughput.WithMetadata("The input of items")
    override val primaryInput by Input<T>()

    @Throughput.WithMetadata("The first output")
    val output1 by Output<T>()

    @Throughput.WithMetadata("The second output")
    val output2 by Output<T>()

    private val outputs by lazy { Pair(output1, output2) }

    override fun loopCondition() : Boolean = !outputs.allClosed

    override suspend fun mainLoopInner() {
        val item = primaryInput.pullOrAbort()

        output1.push(item)
        output2.push(item)
    }


}
