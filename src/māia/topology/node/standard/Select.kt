package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.ConfigurationElement
import māia.configure.ConfigurationItem
import māia.configure.asReconfigureBlock
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.io.Inputs
import māia.topology.io.Output
import māia.topology.io.Throughput
import māia.topology.node.base.ContinuousLoopNode
import māia.topology.node.base.WithPrimaryOutput

/**
 * TODO: What class does.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
@Node.WithMetadata("Listens on a number of arrayed inputs and forwards all items on the same output.")
class Select<T>: ContinuousLoopNode<SelectConfiguration>, WithPrimaryOutput<T> {

    @Configurable.Register<Select<*>, SelectConfiguration>(Select::class, SelectConfiguration::class)
    constructor(block : SelectConfiguration.() -> Unit = {}) : super(block)

    constructor(config : SelectConfiguration) : this(config.asReconfigureBlock())

    @Throughput.WithMetadata("The arrayed inputs")
    val inputs by Inputs<T>(configuration.size)

    @Throughput.WithMetadata("The primary output")
    override val primaryOutput by Output<T>()

    override fun loopCondition() : Boolean = !primaryOutput.isClosed

    override suspend fun mainLoopInner() {
        inputs.selectOrAbort { _, value ->
            primaryOutput.push(value)
        }
    }
}

class SelectConfiguration : NodeConfiguration("select") {

    @ConfigurationElement.WithMetadata("The number of inputs to select from")
    var size by ConfigurationItem { 2 }

    override fun checkIntegrity() : String? {
        return if (size == 0)
            "Can't select from no inputs"
        else if (size < 0)
            "Can't select from a negative number of inputs ($size)"
        else
            null
    }

}
