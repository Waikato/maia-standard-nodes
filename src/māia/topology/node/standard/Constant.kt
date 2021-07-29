package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.ConfigurationElement
import māia.configure.ConfigurationItem
import māia.configure.asReconfigureBlock
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.node.base.Source


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
