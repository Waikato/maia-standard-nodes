package maia.topology.node.standard

import maia.configure.Configurable
import maia.configure.asReconfigureBlock
import maia.topology.ExecutionState
import maia.topology.NodeConfiguration
import maia.topology.io.Input
import maia.topology.io.Output
import maia.topology.io.util.allClosed
import maia.topology.io.util.multiPush
import maia.topology.node.base.ContinuousLoopNode
import maia.util.Absent
import maia.util.Optional
import maia.util.Present

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
