package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.ConfigurationElement
import māia.configure.ConfigurationItem
import māia.configure.asReconfigureBlock
import māia.topology.Node
import māia.topology.NodeConfiguration
import māia.topology.node.base.Sink

/**
 * TODO: What class does.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
@Node.WithMetadata("Prints any items it receives to std::out.")
class Printer : Sink<PrinterConfiguration, Any?> {

    @Configurable.Register<Printer, PrinterConfiguration>(Printer::class, PrinterConfiguration::class)
    constructor(block : PrinterConfiguration.() -> Unit) : super(block)

    constructor(configuration : PrinterConfiguration) : this(configuration.asReconfigureBlock())

    override suspend fun consume(item: Any?) {
        println("${configuration.prefix.format(name, fullName)}$item${configuration.suffix.format(name, fullName)}")
    }

}

class PrinterConfiguration : NodeConfiguration("printer") {

    @ConfigurationElement.WithMetadata("The prefix")
    var prefix by ConfigurationItem { "%2\$s says: " }

    @ConfigurationElement.WithMetadata("The suffix")
    var suffix by ConfigurationItem { "" }

}
