package māia.topology.node.standard

import māia.configure.Configurable
import māia.topology.NodeConfiguration
import māia.topology.node.base.LockStepTransformer

/**
 * TODO: What class does.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
class OneShot<T> : LockStepTransformer<NodeConfiguration, T, T> {

    @Configurable.Register<OneShot<*>, NodeConfiguration>(OneShot::class, NodeConfiguration::class)
    constructor(block : NodeConfiguration.() -> Unit) : super(block)

    override suspend fun transformSingle(item: T): T {
        // Stop the main loop immediately after the first iteration
        stop()

        // Return the item unchanged
        return item
    }

}
