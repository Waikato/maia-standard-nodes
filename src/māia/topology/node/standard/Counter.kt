package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.asReconfigureBlock
import māia.topology.ExecutionState
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.node.base.Source

/**
 * TODO: What class does.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
@Node.WithMetadata("Source of consecutive integers")
class Counter : Source<NodeConfiguration, Int> {

    @Configurable.Register<Counter, NodeConfiguration>(Counter::class, NodeConfiguration::class)
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : NodeConfiguration) : this(config.asReconfigureBlock())

    private var count by ExecutionState { 0 }

    override suspend fun produce(): Int {
        return count++
    }

}
