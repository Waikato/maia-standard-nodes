package maia.topology.node.standard.routing

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import maia.configure.Configurable
import maia.configure.ConfigurationElement
import maia.configure.ConfigurationItem
import maia.configure.asReconfigureBlock
import maia.topology.ExecutionState
import maia.topology.Node
import maia.topology.NodeConfiguration
import maia.topology.io.Input
import maia.topology.io.Output
import maia.topology.io.Throughput
import maia.topology.io.error.InputClosedDuringPullException
import maia.topology.node.base.WithPrimaryInput
import maia.topology.node.base.WithPrimaryOutput

/**
 * TODO: What class does.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
@Node.WithMetadata("Buffers items in memory")
class Buffer<T> : Node<BufferConfiguration>, WithPrimaryInput<T>, WithPrimaryOutput<T> {

    @Configurable.Register<Buffer<*>, BufferConfiguration>(Buffer::class, BufferConfiguration::class)
    constructor(block : BufferConfiguration.() -> Unit = {}) : super(block)

    constructor(config : BufferConfiguration) : this(config.asReconfigureBlock())

    @Throughput.WithMetadata("The input to the buffer")
    override val primaryInput by Input<T>()

    @Throughput.WithMetadata("The output of the buffer")
    override val primaryOutput by Output<T>()

    /** The channel that will perform the buffering. */
    private val bufferChannel by ExecutionState<Channel<T>> { Channel(configuration.size) }

    override suspend fun main() {
        coroutineScope {
            launch {
                handleInput()
            }
            launch {
                handleOutput()
            }
        }
    }

    /**
     * Handles continuously receiving items from the input
     * and putting them in the buffer.
     */
    private suspend fun handleInput() {
        while (true) {
            // Get an item from the input
            val item : T
            try {
                item = primaryInput.pull()
            } catch (e : InputClosedDuringPullException) {
                bufferChannel.close()
                break
            }

            // Add it to the buffer
            try {
                bufferChannel.send(item)
            } catch (e : ClosedSendChannelException) {
                primaryInput.close()
                break
            }
        }
    }

    /**
     * Handles continuously retrieving items from the buffer
     * and posting them to the output.
     */
    private suspend fun handleOutput() {
        while (true) {
            // Get an item from the buffer
            val item : T
            try {
                item = bufferChannel.receive()
            } catch (e : ClosedReceiveChannelException) {
                primaryOutput.close()
                break
            }

            // Push it to the output
            if (!primaryOutput.push(item)) {
                drainBuffer()
                break
            }
        }
    }

    /**
     * Closes and drains the buffer channel.
     */
    private suspend fun drainBuffer() {
        bufferChannel.close()
        while (!bufferChannel.isClosedForReceive) {
            bufferChannel.receive()
        }
    }
}

class BufferConfiguration : NodeConfiguration("buffer") {

    @ConfigurationElement.WithMetadata("The number of items to buffer")
    var size by ConfigurationItem { 1 }

    override fun checkIntegrity() : String? {
        return super.checkIntegrity() ?:
                checkSize()
    }

    private fun checkSize() : String? {
        return if (size < 0)
            "Buffer size can't be negative (got $size)"
        else
            null
    }

}
