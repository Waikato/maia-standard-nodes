package maia.topology.node.standard

import maia.configure.Configurable
import maia.configure.asReconfigureBlock
import maia.topology.NodeConfiguration
import maia.topology.node.base.LockStepTransformer

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
