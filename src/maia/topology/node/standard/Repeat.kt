package maia.topology.node.standard

import maia.configure.Configurable
import maia.configure.ConfigurationItem
import maia.configure.asReconfigureBlock
import maia.topology.NodeConfiguration
import maia.topology.node.base.Transformer
import maia.util.CountingIterator

/**
 * TODO: What class does.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
class Repeat<T> : Transformer<RepeatConfiguration, T, T> {

    @Configurable.Register<Repeat<*>, RepeatConfiguration>(Repeat::class, RepeatConfiguration::class)
    constructor(block : RepeatConfiguration.() -> Unit = {}) : super(block)

    constructor(configuration : RepeatConfiguration) : this(configuration.asReconfigureBlock())

    override suspend fun transform(item: T): Iterator<T> {
        return CountingIterator(configuration.times) { item }
    }

}

class RepeatConfiguration : NodeConfiguration("repeat") {

    var times by ConfigurationItem { -1L }

}
