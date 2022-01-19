package maia.topology.node.standard

import maia.configure.Configurable
import maia.configure.ConfigurationElement
import maia.configure.ConfigurationItem
import maia.configure.asReconfigureBlock
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.node.base.Source


@Node.WithMetadata("Continuously returns a constant value")
class Constant<T> : Source<ConstantConfiguration<T>, T> {

    @Configurable.Register<Constant<*>, ConstantConfiguration<*>>(
        Constant::class,
        ConstantConfiguration::class
    )
    constructor(block : ConstantConfiguration<T>.() -> Unit = {}) : super(block)

    constructor(config : ConstantConfiguration<T>) : super(config.asReconfigureBlock())

    override suspend fun produce() : T {
        return configuration.constant
    }

}

class ConstantConfiguration<T> : NodeConfiguration("constant") {

    @ConfigurationElement.WithMetadata("The constant to output")
    var constant by ConfigurationItem<T>()

}
