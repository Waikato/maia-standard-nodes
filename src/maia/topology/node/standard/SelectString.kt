package maia.topology.node.standard

import maia.configure.Configurable
import maia.configure.ConfigurationItem
import maia.configure.asReconfigureBlock
import maia.topology.NodeConfiguration
import maia.topology.node.base.LockStepTransformer

class SelectString : LockStepTransformer<SelectStringConfiguration, Int, String> {

    @Configurable.Register<SelectString, SelectStringConfiguration>(SelectString::class, SelectStringConfiguration::class)
    constructor(block : SelectStringConfiguration.() -> Unit = {}) : super(block)

    constructor(config : SelectStringConfiguration) : this(config.asReconfigureBlock())

    private val options = configuration.options.split(",").toTypedArray()

    override suspend fun transformSingle(item: Int): String {
        return options[item]
    }

}

class SelectStringConfiguration : NodeConfiguration("selectString") {

    var options by ConfigurationItem { "" }

}
