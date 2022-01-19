package maia.topology.node.standard.test

import maia.configure.Configurable
import maia.configure.asReconfigureBlock
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.node.base.Source


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
