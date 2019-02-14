import Node.Companion.B
import Node.Companion.WIDTH
import Node.Companion.createSingleLeaves
import Node.Companion.getIndex

class FastTreeList<T> internal constructor(
    private val level: Int,
    private var nodes: Array<Node<T>?>?,
    private var nodesLen: Int,
    private var tail: Array<Any?>,
    private var tailLen: Int
) : List<T> {

    override val size: Int
        get() = nodesLen + tailLen

    override fun isEmpty(): Boolean = size == 0

    override operator fun get(index: Int): T {
        return if (index < nodesLen) {
            if (index < 0) {
                throw IndexOutOfBoundsException("Index $index out of bounds")
            }
            val level = level
            val i = getIndex(level, index)
            nodes!![i]!!.get(level - WIDTH, index)
        } else {
            val tailIndex = index - nodesLen
            if (tailIndex >= tailLen) {
                // indexを忘れてもいいようにtailIndexを使って計算する
                throw IndexOutOfBoundsException("Index ${tailIndex + nodesLen} out of boundds for size $size")
            }
            @Suppress("UNCHECKED_CAST")
            tail[tailIndex] as T
        }
    }

    fun added(e: T): FastTreeList<T> {
        val tailLen = this.tailLen
        val tail = this.tail
        if (tailLen < tail.size) {
            // tailが空いている場合
            val newTail = tail.copyOf()
            newTail[tailLen] = e
            return FastTreeList(level, nodes, nodesLen, newTail, tailLen + 1)
        } else {
            val nodes = this.nodes
            if (nodes === null) {
                // nodesが未初期化状態の場合
                val newNodes = arrayOfNulls<Node<T>>(B).also { it[0] = Node(null, tail) }
                return FastTreeList(level + WIDTH, newNodes, B, arrayOfNulls(B), 0)
            } else if (nodesLen == 1 shl level) {
                // nodesが埋まっている場合
                val oldRoot = Node(nodes, null)
                val second = Node.createSingleLeaves<T>(level, tail)
                val newNodes = arrayOfNulls<Node<T>>(B).also {
                    it[0] = oldRoot
                    it[1] = second
                }
                return FastTreeList(level + WIDTH, newNodes, nodesLen + B, arrayOfNulls(B), 0)
            } else {
                // nodesに空きがある場合
                val level = this.level
                val nodesLen = this.nodesLen
                val index = getIndex(level, nodesLen)
                val newNodes = nodes.copyOf()
                newNodes[index] = newNodes[index]?.addedLeaves(level - WIDTH, size, tail)
                    ?: createSingleLeaves(level - WIDTH, tail)
                return FastTreeList(level, newNodes, nodesLen + B, arrayOfNulls(B), 0)
            }
        }
    }

    override fun contains(element: T): Boolean {
        TODO("not implemented")
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        TODO("not implemented")
    }


    override fun indexOf(element: T): Int {
        TODO("not implemented")
    }

    override fun iterator(): Iterator<T> {
        TODO("not implemented")
    }

    override fun lastIndexOf(element: T): Int {
        TODO("not implemented")
    }

    override fun listIterator(): ListIterator<T> {
        TODO("not implemented")
    }

    override fun listIterator(index: Int): ListIterator<T> {
        TODO("not implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        TODO("not implemented")
    }
}
