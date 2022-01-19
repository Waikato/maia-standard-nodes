package maia.topology.node.standard.test

import maia.configure.Configurable
import maia.configure.asReconfigureBlock
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.node.base.Sink

/**
 * Sink which just consumes any item without processing it at all.
 */
@Node.WithMetadata("Sink which just consumes any item without processing it at all")
class Void : Sink<NodeConfiguration, Any?> {

    @Configurable.Register<Void, NodeConfiguration>(
        Void::class,
        NodeConfiguration::class
    )
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : NodeConfiguration) : this(config.asReconfigureBlock())

    override suspend fun consume(item: Any?) {
        // Do nothing
    }

}
