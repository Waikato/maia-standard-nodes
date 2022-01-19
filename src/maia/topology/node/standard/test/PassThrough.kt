package maia.topology.node.standard.test

import maia.configure.Configurable
import maia.configure.asReconfigureBlock
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.node.base.LockStepTransformer

/**
 * Node which passes items through without touching them.
 */
@Node.WithMetadata("Node which passes items through without touching them")
class PassThrough<T> : LockStepTransformer<NodeConfiguration, T, T> {

    @Configurable.Register<PassThrough<*>, NodeConfiguration>(PassThrough::class, NodeConfiguration::class)
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : NodeConfiguration) : this(config.asReconfigureBlock())

    override suspend fun transformSingle(item : T) : T {
        return item
    }
}
