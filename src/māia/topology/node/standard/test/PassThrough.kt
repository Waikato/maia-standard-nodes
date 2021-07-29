package māia.topology.node.standard.test

import māia.configure.Configurable
import māia.configure.asReconfigureBlock
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.node.base.LockStepTransformer

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
