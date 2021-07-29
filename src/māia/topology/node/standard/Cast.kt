package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.asReconfigureBlock
import māia.topology.NodeConfiguration
import māia.topology.node.base.LockStepTransformer

/**
 * TODO: What class does.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
class Cast<I, O> : LockStepTransformer<NodeConfiguration, I, O> {

    @Configurable.Register<Cast<*, *>, NodeConfiguration>(Cast::class, NodeConfiguration::class)
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : NodeConfiguration) : this(config.asReconfigureBlock())

    override suspend fun transformSingle(item : I) : O {
        return item as O
    }

}
