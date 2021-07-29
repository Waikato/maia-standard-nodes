package māia.topology.node.standard

import māia.configure.Configurable
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.io.Input
import māia.topology.io.Output

/**
 * TODO: What class does.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
class Each<T> : Node<NodeConfiguration> {

    @Configurable.Register<Each<*>, NodeConfiguration>(Each::class, NodeConfiguration::class)
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    val input by Input<Iterable<T>>()

    val output by Output<T>()

    override suspend fun main() {
        while(!input.isClosed && !output.isClosed) {
            val iter = input.pull()

            for (element in iter) {
                if (output.isClosed) abort()
                output.push(element)
            }
        }
    }

}
