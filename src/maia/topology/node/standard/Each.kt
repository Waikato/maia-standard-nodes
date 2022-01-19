package maia.topology.node.standard

import maia.configure.Configurable
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.io.Input
import maia.topology.io.Output

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
