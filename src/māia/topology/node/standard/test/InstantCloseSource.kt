package māia.topology.node.standard.test

import māia.configure.Configurable
import māia.configure.asReconfigureBlock
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.node.base.Source


/**
 * Source which does nothing and closes its output immediately.
 */
@Node.WithMetadata("Source which does nothing and closes its output immediately")
class InstantCloseSource<T> : Source<NodeConfiguration, T> {

    @Configurable.Register<InstantCloseSource<*>, NodeConfiguration>(
        InstantCloseSource::class,
        NodeConfiguration::class
    )
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : NodeConfiguration) : this(config.asReconfigureBlock())

    override suspend fun preLoop() {
        abort()
    }

    override suspend fun produce(): T {
        abort() // Does nothing
    }

}
