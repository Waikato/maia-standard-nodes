package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.ConfigurationItem
import māia.configure.asReconfigureBlock
import māia.topology.NodeConfiguration
import māia.topology.node.base.Transformer
import māia.util.CountingIterator

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
