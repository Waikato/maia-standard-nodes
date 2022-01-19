package maia.topology.node.standard

import maia.configure.Configurable
import maia.configure.ConfigurationItem
import maia.configure.asReconfigureBlock
import maia.topology.ExecutionState
import maia.topology.NodeConfiguration
import maia.topology.node.base.Sink
import java.io.FileOutputStream
import java.io.ObjectOutputStream

/**
 * TODO: What class does.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
class SaveSerialisable : Sink<SaveSerialisableConfiguration, Any> {

    @Configurable.Register<SaveSerialisable, SaveSerialisableConfiguration>(
        SaveSerialisable::class, SaveSerialisableConfiguration::class)
    constructor(block : SaveSerialisableConfiguration.() -> Unit = {}) : super(block)

    constructor(config : SaveSerialisableConfiguration) : this(config.asReconfigureBlock())

    val output by ExecutionState {
        ObjectOutputStream(FileOutputStream(configuration.filename))
    }

    override suspend fun consume(item : Any) {
        output.writeObject(item)
    }

}

class SaveSerialisableConfiguration : NodeConfiguration("saveSerialisable") {

    var filename by ConfigurationItem { "serialisable.dat" }

}
