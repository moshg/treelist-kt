package mosh.treelist

import mosh.treelist.Node.Companion.getIndex

class TreeListDeque<T> internal constructor(
    private val level: Int,
    private val nodes: Array<Node<T>?>,
    private val nodesStart: Int,
    private val nodesLen: Int,
    private val head: Array<Any?>,
    private val headLen: Int,
    private val tail: Array<Any?>,
    private val tailLen: Int
) : List<T> {

    constructor() : this(
        0,
        TreeList.emptyNodes(), B ushr 1, B ushr 1,
        TreeList.emptyTail(), 0,
        TreeList.emptyTail(), 0
    )

    override val size: Int
        get() = nodesLen + headLen + tailLen

    private val headStart: Int
        get() = B - headLen

    override fun isEmpty(): Boolean =
        size == 0

    private fun indexUnderflowException(index: Int) =
        IndexOutOfBoundsException("Index $index out of bounds")

    private fun indexOverflowException(index: Int) =
        IndexOutOfBoundsException("Index $index out of bounds for length $size")

    private fun copy(
        level: Int = this.level,
        nodes: Array<Node<T>?> = this.nodes,
        nodesStart: Int = this.nodesStart,
        nodesLen: Int = this.nodesLen,
        head: Array<Any?> = this.head,
        headLen: Int = this.headLen,
        tail: Array<Any?> = this.tail,
        tailLen: Int = this.tailLen
    ): TreeListDeque<T> =
        TreeListDeque(level, nodes, nodesStart, nodesLen, head, headLen, tail, tailLen)

    @Suppress("UNCHECKED_CAST")
    override operator fun get(index: Int): T {
        return if (index < headLen) {
            if (index < 0) {
                throw indexUnderflowException(index)
            }
            head[headStart + index] as T
        } else if (index >= headLen + nodesLen) {
            if (index >= size) {
                throw indexOverflowException(index)
            }
            tail[index - headLen - nodesLen] as T
        } else {
            val level = level
            val i = getIndex(level, index - headLen) + nodesStart
            nodes[i]!!.get(level - WIDTH, index - headLen)
        }
    }

    fun set(index: Int, e: T): TreeListDeque<T> {
        return if (index < headLen) {
            if (index < 0) {
                throw indexUnderflowException(index)
            }
            val newHead = head.copyOf()
            newHead[headStart + index] = e
            copy(head = newHead)
        } else if (index >= headLen + nodesLen) {
            if (index >= size) {
                throw indexOverflowException(index)
            }
            val newTail = tail.copyOf()
            newTail[index - headLen - nodesLen] = e
            copy(tail = newTail)
        } else {
            val level = level
            val newNodes = nodes.copyOf()
            val i = getIndex(level, index - headLen) + nodesStart
            newNodes[i] = newNodes[i]!!.set(level - WIDTH, index - headLen, e)
            copy(nodes = newNodes)
        }
    }
}
