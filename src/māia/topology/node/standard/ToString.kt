package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.asReconfigureBlock
import māia.topology.NodeConfiguration
import māia.topology.node.base.LockStepTransformer

class ToString : LockStepTransformer<NodeConfiguration, Any?, String> {

    @Configurable.Register<ToString, NodeConfiguration>(ToString::class, NodeConfiguration::class)
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : NodeConfiguration) : this(config.asReconfigureBlock())

    override suspend fun transformSingle(item: Any?): String {
        return item.toString()
    }
}
