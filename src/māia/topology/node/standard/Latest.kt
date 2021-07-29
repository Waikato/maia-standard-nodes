package māia.topology.node.standard

import māia.configure.Configurable
import māia.configure.asReconfigureBlock
import māia.topology.ExecutionState
import māia.topology.NodeConfiguration
import māia.topology.io.Input
import māia.topology.io.Output
import māia.topology.io.util.allClosed
import māia.topology.io.util.multiPush
import māia.topology.node.base.ContinuousLoopNode
import māia.util.Absent
import māia.util.Optional
import māia.util.Present

class Latest<T, S> : ContinuousLoopNode<NodeConfiguration> {

    @Configurable.Register<Latest<*, *>, NodeConfiguration>(Latest::class, NodeConfiguration::class)
    constructor(block : NodeConfiguration.() -> Unit = {}) : super(block)

    constructor(config : NodeConfiguration) : this(config.asReconfigureBlock())

    val triggerInput by Input<T>()

    val sourceInput by Input<S>()

    val triggerOutput by Output<T>()

    val sourceOutput by Output<S>()

    var latestValue by ExecutionState<Optional<S>> { Absent }

    private val inputList by lazy { listOf(triggerInput, sourceInput) }

    val outputPair by lazy { Pair(triggerOutput, sourceOutput) }

    override suspend fun preLoop() {
        super.preLoop()
        latestValue = Present(sourceInput.pullOrAbort())
    }

    override fun loopCondition() : Boolean = !outputPair.allClosed

    override suspend fun mainLoopInner() {
        inputList.selectOrAbort { input, value ->
            when (input) {
                sourceInput -> {
                    latestValue = Present(value as S)
                    continueNodeLoop()
                }
                triggerInput -> {
                    outputPair.multiPush(value as T, latestValue.get())
                }
            }
        }
    }

}
