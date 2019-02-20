package mosh.treelist

import mosh.treelist.Node.Companion.createSingleLeaves
import mosh.treelist.Node.Companion.getIndex

class TreeList<T> internal constructor(
    private val level: Int,
    private val nodes: Array<Node<T>?>,
    private val nodesLen: Int,
    private val tail: Array<Any?>,
    private val tailLen: Int
) : List<T> {

    constructor() : this(0, emptyNodes(), 0, emptyTail(), 0)

    override val size: Int
        get() = nodesLen + tailLen

    override fun isEmpty(): Boolean = size == 0

    private fun indexUnderflowException(index: Int) = IndexOutOfBoundsException("Index $index out of bounds")

    private fun indexOverflowException(index: Int) =
        IndexOutOfBoundsException("Index $index out of bounds for length $size")

    override operator fun get(index: Int): T {
        return if (index < nodesLen) {
            if (index < 0) {
                throw indexUnderflowException(index)
            }
            val level = level
            val i = getIndex(level, index)
            nodes[i]!!.get(level - WIDTH, index)
        } else {
            val tailIndex = index - nodesLen
            if (tailIndex >= tailLen) {
                // indexを忘れてもいいようにtailIndexを使って計算する
                throw indexOverflowException(tailIndex + nodesLen)
            }
            @Suppress("UNCHECKED_CAST")
            tail[tailIndex] as T
        }
    }

    fun set(index: Int, e: T): TreeList<T> {
        val nodesLen = this.nodesLen
        if (index < nodesLen) {
            if (index < 0) {
                throw indexUnderflowException(index)
            }
            val level = level
            val newNodes = nodes.copyOf()
            val i = getIndex(level, index)
            newNodes[i] = newNodes[i]!!.set(level - WIDTH, index, e)
            return TreeList(level, newNodes, nodesLen, tail, tailLen)
        } else {
            val tailIndex = index - nodesLen
            if (tailIndex >= tailLen) {
                // indexを忘れてもいいようにtailIndexを使って計算する
                throw indexOverflowException(tailIndex + nodesLen)
            }
            val newTail = tail.copyOf()
            newTail[tailIndex] = e
            return TreeList(level, nodes, nodesLen, newTail, tailLen)
        }
    }

    fun added(e: T): TreeList<T> {
        val tailLen = this.tailLen
        val tail = this.tail
        if (tailLen < tail.size) {
            // tailが空いている場合
            val newTail = tail.copyOf()
            newTail[tailLen] = e
            return TreeList(level, nodes, nodesLen, newTail, tailLen + 1)
        } else {
            val newTail = arrayOfNulls<Any?>(B).also { it[0] = e }
            val nodes = this.nodes
            if (nodes === emptyNodes<Node<T>?>()) {
                // nodesが未初期化状態の場合
                val newNodes = arrayOfNulls<Node<T>>(B).also {
                    it[0] = Node(null, tail)
                }
                return TreeList(level + WIDTH, newNodes, B, newTail, 1)
            } else if (nodesLen == 1 shl (level + WIDTH)) {
                // nodesが埋まっている場合
                val oldRoot = Node(nodes, null)
                val second = Node.createSingleLeaves<T>(level, tail)
                val newNodes = arrayOfNulls<Node<T>>(B).also {
                    it[0] = oldRoot
                    it[1] = second
                }
                return TreeList(
                    level + WIDTH,
                    newNodes,
                    nodesLen + B,
                    newTail,
                    1
                )
            } else {
                // nodesに空きがある場合
                val level = this.level
                val nodesLen = this.nodesLen
                val index = getIndex(level, nodesLen)
                val newNodes = nodes.copyOf()
                newNodes[index] = newNodes[index]?.addedLeaves(level - WIDTH, nodesLen, tail)
                    ?: createSingleLeaves(level - WIDTH, tail)
                return TreeList(level, newNodes, nodesLen + B, newTail, 1)
            }
        }
    }

    fun addedAll(elements: Iterable<T>): TreeList<T> = addedWith {
        for (e in elements) {
            add(e)
        }
    }

    override fun contains(element: T): Boolean {
        if (element === null) {
            for (e in this) {
                if (e === null) {
                    return true
                }
            }
            return false
        } else {
            for (e in this) {
                if (element == e) {
                    return true
                }
            }
            return false
        }
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        val set = HashSet<T>(size)
        set.addAll(this)
        return set.containsAll(elements)
    }

    override fun indexOf(element: T): Int {
        var index = 0
        if (element === null) {
            val iter = iterator()
            while (iter.hasNext()) {
                if (iter.next() === null) {
                    return index
                }
                index += 1
            }
            return -1
        } else {
            val iter = iterator()
            while (iter.hasNext()) {
                if (element == iter.next()) {
                    return index
                }
                index += 1
            }
            return -1
        }
    }

    override fun lastIndexOf(element: T): Int {
        var index = size
        val iter = listIterator(size)
        if (element === null) {
            while (iter.hasPrevious()) {
                if (iter.previous() === null) {
                    return index - 1
                }
                index -= 1
            }
            return -1
        } else {
            while (iter.hasPrevious()) {
                if (element == iter.previous()) {
                    return index - 1
                }
                index -= 1
            }
            return -1
        }
    }

    override fun iterator(): Iter<T> {
        val focus = arrayOfNulls<Array<Node<T>?>>(level / WIDTH)
        val leaves: Array<Any?>?
        if (nodes[0] === null) {
            leaves = tail
        } else {
            focus[focus.size - 1] = nodes
            for (i in (focus.size - 1) downTo 1) {
                focus[i - 1] = focus[i]!![0]!!.nodes
            }
            leaves = focus[0]!![0]!!.leaves
        }
        @Suppress("UNCHECKED_CAST")
        return Iter(
            focus as Array<Array<Node<T>?>>,
            nodesLen,
            tail,
            tailLen,
            leaves
        )
    }


    override fun listIterator(): ListIter<T> =
        ListIter(level, nodes, nodesLen, tail, tailLen, null, 0)

    override fun listIterator(index: Int): ListIterator<T> {
        if (index < nodesLen) {
            if (index < 0) {
                throw indexUnderflowException(index)
            }
            return ListIter(
                level,
                nodes,
                nodesLen,
                tail,
                tailLen,
                nodes[getIndex(level, index)]!!.getLeaves(level - WIDTH, index),
                index
            )
        } else {
            if (index > size) {
                throw indexOverflowException(index)
            }
            return ListIter(level, nodes, nodesLen, tail, tailLen, null, index)
        }
    }

    class Iter<T> internal constructor(
        private val focus: Array<Array<Node<T>?>>,
        private val nodesLen: Int,
        private val tail: Array<Any?>,
        private val tailLen: Int,
        private var leaves: Array<Any?>?
    ) : Iterator<T> {

        private var index: Int = 0
        private var jump: Int = B

        private val size: Int
            get() = nodesLen + tailLen

        override fun hasNext(): Boolean = index < size

        @Suppress("UNCHECKED_CAST")
        override fun next(): T {
            val index = this.index
            if (index >= nodesLen) {
                if (index >= nodesLen + tailLen) {
                    throw NoSuchElementException("Index $index out of bounds for size ${nodesLen + tailLen}")
                } else {
                    this.index = index + 1
                    return tail[index and MASK] as T
                }
            }

            if (index != jump) {
                this.index = index + 1
                return leaves!![index and MASK] as T
            }
            jump += B
            val diff = index xor (index - 1)
            var level = WIDTH * 2
            var focusIndex = 0
            while ((diff ushr level) != 0) {
                level += WIDTH
                focusIndex += 1
            }
            level -= WIDTH

            while (focusIndex > 0) {
                focus[focusIndex - 1] = focus[focusIndex][getIndex(level, index)]!!.nodes!!
                level -= WIDTH
                focusIndex -= 1
            }

            leaves = focus[0][getIndex(WIDTH, index)]!!.leaves

            this.index = index + 1
            return leaves!![index and MASK] as T
        }
    }

    class ListIter<T> internal constructor(
        private val level: Int,
        private val nodes: Array<Node<T>?>,
        private val nodesLen: Int,
        private val tail: Array<Any?>,
        private val tailLen: Int,
        private var leaves: Array<Any?>?,
        private var index: Int
    ) : ListIterator<T> {

        private val size: Int
            get() = nodesLen + tailLen

        override fun hasNext(): Boolean = index < size

        override fun nextIndex(): Int = index

        override fun next(): T {
            val index = this.index
            if (index < nodesLen) {
                this.index = index + 1
                val leafIndex = index and MASK
                if (leafIndex == 0) {
                    leaves = nodes[getIndex(level, index)]!!.getLeaves(level - WIDTH, index)
                    @Suppress("UNCHECKED_CAST")
                    return leaves!![0] as T
                } else {
                    @Suppress("UNCHECKED_CAST")
                    return leaves!![leafIndex] as T
                }
            } else {
                val tailIndex = index - nodesLen
                if (tailIndex >= tailLen) {
                    throw NoSuchElementException("Index $index out of bounds for size $size")
                }
                this.index = index + 1
                @Suppress("UNCHECKED_CAST")
                return tail[tailIndex] as T
            }
        }

        override fun hasPrevious(): Boolean = index > 0

        override fun previousIndex(): Int = index - 1

        override fun previous(): T {
            val index = this.index - 1
            if (index < nodesLen) {
                if (index < 0) {
                    throw NoSuchElementException("Index $index out of bounds")
                }
                this.index = index
                val leafIndex = index and MASK
                if (leafIndex == MASK) {
                    val leaves = nodes[getIndex(level, index)]!!.getLeaves(level - WIDTH, index)
                    this.leaves = leaves
                    @Suppress("UNCHECKED_CAST")
                    return leaves[leaves.size - 1] as T
                } else {
                    @Suppress("UNCHECKED_CAST")
                    return leaves!![leafIndex] as T
                }
            } else {
                this.index = index
                val tailIndex = index - nodesLen
                @Suppress("UNCHECKED_CAST")
                return tail[tailIndex] as T
            }
        }
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        TODO("not implemented")
    }

    fun builder(): TreeListBuilder<T> {
        if (tailLen == 0) {
            // 空のとき
            return TreeListBuilder(0, null, 0, null, 0)
        } else if (nodesLen == 0) {
            // nodesだけが空のとき
            return TreeListBuilder(0, null, 0, tail.copyOf(), tailLen)
        } else if (nodesLen == 1 shl (level + WIDTH)) {
            // nodesがいっぱいのとき
            return TreeListBuilder(level, nodes, nodesLen, tail.copyOf(), tailLen)
        } else {
            // 最後のノードのからルートまでのノードとテイルをコピーする
            val newNodes = nodes.copyOf()
            val index = nodesLen and MASK
            val end = newNodes[index]
            if (end !== null) {
                newNodes[index] = end.copy(level - WIDTH, nodesLen)
            }
            return TreeListBuilder(level, newNodes, nodesLen, tail.copyOf(), tailLen)
        }
    }

    fun addedWith(block: TreeListBuilder<T>.() -> Unit): TreeList<T> {
        val b = builder()
        block(b)
        return b.build()
    }

    companion object {
        private val emptyNodes: Array<Node<*>?> = arrayOfNulls(B)

        @Suppress("UNCHECKED_CAST")
        internal fun <T> emptyNodes(): Array<Node<T>?> = emptyNodes as Array<Node<T>?>

        private val emptyTail: Array<Any?> = arrayOfNulls(B)

        @Suppress("UNCHECKED_CAST")
        internal fun emptyTail(): Array<Any?> = emptyTail
    }
}
