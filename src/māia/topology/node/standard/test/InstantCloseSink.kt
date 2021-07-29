package māia.topology.node.standard.test

import māia.configure.Configurable
import māia.configure.asReconfigureBlock
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.node.base.Sink

/**
 * Sink which does nothing and closes its input immediately.
 */
@Node.WithMetadata("Sink which does nothing and closes its input immediately")
class InstantCloseSink : Sink<NodeConfiguration, Any?> {

    @Configurable.Register<InstantCloseSink, NodeConfiguration>(
        InstantCloseSink::class,
        NodeConfiguration::class
    )
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : NodeConfiguration) : this(config.asReconfigureBlock())

    override suspend fun preLoop() {
        abort()
    }

    override suspend fun consume(item: Any?) {
        // Does nothing
    }

}
