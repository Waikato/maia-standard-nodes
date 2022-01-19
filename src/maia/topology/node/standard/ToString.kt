package maia.topology.node.standard

import maia.configure.Configurable
import maia.configure.asReconfigureBlock
import maia.topology.NodeConfiguration
import maia.topology.node.base.LockStepTransformer

class ToString : LockStepTransformer<NodeConfiguration, Any?, String> {

    @Configurable.Register<ToString, NodeConfiguration>(ToString::class, NodeConfiguration::class)
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : NodeConfiguration) : this(config.asReconfigureBlock())

    override suspend fun transformSingle(item: Any?): String {
        return item.toString()
    }
}
