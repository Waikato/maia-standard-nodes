package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.ConfigurationItem
import māia.configure.asReconfigureBlock
import māia.topology.NodeConfiguration
import māia.topology.node.base.LockStepTransformer
import kotlin.random.Random

class RandRange : LockStepTransformer<RandRangeConfiguration, Any?, Int> {

    @Configurable.Register<RandRange, RandRangeConfiguration>(RandRange::class, RandRangeConfiguration::class)
    constructor(block : RandRangeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : RandRangeConfiguration) : this(config.asReconfigureBlock())

    private val random = if (configuration.useDefault) Random.Default else Random(configuration.seed)

    override suspend fun transformSingle(item: Any?): Int {
        return random.nextInt(configuration.from, configuration.to)
    }
}

class RandRangeConfiguration : NodeConfiguration("randRange") {

    var useDefault by ConfigurationItem { false }

    var seed by ConfigurationItem { 0 }

    var from by ConfigurationItem { 0 }

    var to by ConfigurationItem { 6 }

}
