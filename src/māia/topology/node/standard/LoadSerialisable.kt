package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.asReconfigureBlock
import māia.topology.node.base.Source
import java.io.FileInputStream
import java.io.ObjectInputStream

/**
 * TODO: What class does.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
class LoadSerialisable : Source<SaveSerialisableConfiguration, Any> {

    @Configurable.Register<LoadSerialisable, SaveSerialisableConfiguration>(
        LoadSerialisable::class, SaveSerialisableConfiguration::class)
    constructor(block : SaveSerialisableConfiguration.() -> Unit = {}) : super(block)

    constructor(config : SaveSerialisableConfiguration) : this(config.asReconfigureBlock())

    override suspend fun produce() : Any {
        // Only returns the serialisable once then stops
        stop()

        val file = FileInputStream(configuration.filename)
        val input = ObjectInputStream(file)
        return input.readObject()
    }

}
