package maia.topology.node.standard

import maia.configure.Configurable
import maia.configure.asReconfigureBlock
import maia.topology.ExecutionState
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.node.base.Source

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
