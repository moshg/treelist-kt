package mosh.treelist

@Suppress("NAME_SHADOWING")
internal class Node<T>(var nodes: Array<Node<T>?>?, var leaves: Array<Any?>?) {
    @Suppress("NOTHING_TO_INLINE")
    inline fun get(level: Int, index: Int): T = get(this, level, index)

    @Suppress("NOTHING_TO_INLINE")
    inline fun getLeaves(level: Int, index: Int): Array<Any?> =
        getLeaves(this, level, index)

    @Suppress("NOTHING_TO_INLINE")
    inline fun set(level: Int, index: Int, e: T): Node<T> =
        set(this, level, index, e)

    @Suppress("NOTHING_TO_INLINE")
    inline fun add(level: Int, index: Int, e: T) = add(this, level, index, e)

    @Suppress("NOTHING_TO_INLINE")
    inline fun addLeaves(level: Int, index: Int, leaves: Array<Any?>) =
        addLeaves(this, level, index, leaves)

    @Suppress("NOTHING_TO_INLINE")
    inline fun added(level: Int, index: Int, e: T): Node<T> =
        added(this, level, index, e)

    @Suppress("NOTHING_TO_INLINE")
    inline fun addedLeaves(level: Int, index: Int, leaves: Array<Any?>) =
        addedLeaves(this, level, index, leaves)

    @Suppress("NOTHING_TO_INLINE")
    inline fun copy(level: Int, index: Int): Node<T> =
        copy(this, level, index)

    override fun toString(): String = buildString(B * 3) {
        if (leaves === null) {
            append('(')
            append(nodes!![0])
            for (i in 1 until B) {
                val child = nodes!![i]
                if (child === null) {
                    break
                } else {
                    append(", ")
                    append(child)
                }
            }
            append(')')
        } else {
            append('(')
            append(leaves!![0])
            for (i in 1 until B) {
                append(", ")
                // Tがnullableのとき意味のあるnullが入りうるのでブレークしない
                append(leaves!![i])
            }
            append(')')
        }
    }

    companion object {
        @Suppress("NOTHING_TO_INLINE")

        inline fun getIndex(level: Int, i: Int): Int = (i ushr level) and MASK

        fun <T> createSingle(level: Int, e: T): Node<T> =
            createSingleLeaves(
                level,
                arrayOfNulls<Any?>(B).also { it[0] = e })

        fun <T> createSingleLeaves(level: Int, leaves: Array<Any?>): Node<T> {
            var level = level
            var node = Node<T>(null, leaves)
            while (level > 0) {
                val nodes = arrayOfNulls<Node<T>>(B)
                nodes[0] = node
                node = Node(nodes, null)
                level -= WIDTH
            }
            return node
        }

        tailrec fun <T> get(node: Node<T>, level: Int, index: Int): T {
            return if (level == 0) {
                @Suppress("UNCHECKED_CAST")
                node.leaves!![index and MASK] as T
            } else {
                get(
                    node.nodes!![getIndex(level, index)]!!,
                    level - WIDTH,
                    index
                )
            }
        }

        tailrec fun <T> getLeaves(node: Node<T>, level: Int, index: Int): Array<Any?> {
            return if (level == 0) {
                node.leaves!!
            } else {
                getLeaves(
                    node.nodes!![getIndex(
                        level,
                        index
                    )]!!, level - WIDTH, index
                )
            }
        }

        fun <T> set(node: Node<T>, level: Int, index: Int, e: T): Node<T> {
            var level = level
            val retNode = Node<T>(null, null)
            // 現在初期化しているノード
            var currNewNode = retNode
            // 現在コピーしているノード
            var currNode = node

            while (level > 0) {
                val currNewNodes = currNode.nodes!!.copyOf()
                val arrIndex = getIndex(level, index)
                val child = currNewNodes[arrIndex]
                // nextNewNodeをcurrNewNodeのChildrenに追加し, currNewNodeをnextNewNodeに差し替える.
                val nextNewNode = Node<T>(null, null)
                currNewNodes[arrIndex] = nextNewNode
                currNewNode.nodes = currNewNodes
                currNewNode = nextNewNode

                currNode = child!!
                level -= WIDTH
            }

            val newLeaves = currNode.leaves!!.copyOf()
            newLeaves[index and MASK] = e
            currNewNode.leaves = newLeaves

            return retNode
        }

        fun <T> add(node: Node<T>, level: Int, index: Int, e: T) {
            var level = level
            var currNode = node

            while (level > 0) {
                val arrIndex = (index ushr level) and MASK
                val nextNodes = currNode.nodes!!
                val nextNode = nextNodes[arrIndex]
                if (nextNode === null) {
                    nextNodes[arrIndex] = createSingle(level - WIDTH, e)
                    break
                } else {
                    currNode = nextNode
                }
                level -= WIDTH
            }

            val leaves = currNode.leaves!!
            leaves[index and MASK] = e
        }

        fun <T> addLeaves(node: Node<T>, level: Int, index: Int, leaves: Array<Any?>) {
            var level = level
            var currNode = node

            while (level > 0) {
                val arrIndex = (index ushr level) and MASK
                val nextNodes = currNode.nodes!!
                val nextNode = nextNodes[arrIndex]
                if (nextNode === null) {
                    nextNodes[arrIndex] =
                        createSingleLeaves(level - WIDTH, leaves)
                    break
                } else {
                    currNode = nextNode
                }
                level -= WIDTH
            }

            currNode.leaves = leaves
        }

        fun <T> added(node: Node<T>, level: Int, index: Int, e: T): Node<T> {
            var level = level
            val retNode = Node<T>(null, null)
            // 現在初期化しているノード
            var currNewNode = retNode
            // 現在コピーしているノード
            var currNode = node

            while (level > 0) {
                val currNewNodes = currNode.nodes!!.copyOf()
                val arrIndex = (index ushr level) and MASK
                val child = currNewNodes[arrIndex]
                if (child === null) {
                    currNewNodes[arrIndex] = createSingle(level - WIDTH, e)
                    currNewNode.nodes = currNewNodes
                    break
                } else {
                    // nextNewNodeをcurrNewNodeのChildrenに追加し, currNewNodeをnextNewNodeに差し替える.
                    val nextNewNode = Node<T>(null, null)
                    currNewNodes[arrIndex] = nextNewNode
                    currNewNode.nodes = currNewNodes
                    currNewNode = nextNewNode

                    currNode = child
                }
                level -= WIDTH
            }

            if (level == 0) {
                val newLeaves = currNode.leaves!!.copyOf()
                newLeaves[index and MASK] = e
                currNewNode.leaves = newLeaves
            }

            return retNode
        }

        fun <T> addedLeaves(node: Node<T>, level: Int, index: Int, leaves: Array<Any?>): Node<T> {
            var level = level
            val retNode = Node<T>(null, null)
            // 現在初期化しているノード
            var currNewNode = retNode
            // 現在コピーしているノード
            var currNode = node

            while (level > 0) {
                val currNewNodes = currNode.nodes!!.copyOf()
                val arrIndex = (index ushr level) and MASK
                val child = currNewNodes[arrIndex]
                if (child === null) {
                    currNewNodes[arrIndex] =
                        createSingleLeaves(level - WIDTH, leaves)
                    currNewNode.nodes = currNewNodes
                    break
                } else {
                    // nextNewNodeをcurrNewNodeのChildrenに追加し, currNewNodeをnextNewNodeに差し替える.
                    val nextNewNode = Node<T>(null, null)
                    currNewNodes[arrIndex] = nextNewNode
                    currNewNode.nodes = currNewNodes
                    currNewNode = nextNewNode

                    currNode = child
                }
                level -= WIDTH
            }

            if (level == 0) {
                currNewNode.leaves = leaves
            }

            return retNode
        }

        fun <T> copy(node: Node<T>, level: Int, index: Int): Node<T> {
            var level = level
            val retNode = Node<T>(null, null)
            // 現在初期化しているノード
            var currNewNode = retNode
            // 現在コピーしているノード
            var currNode = node

            while (level > 0) {
                val currNewNodes = currNode.nodes!!.copyOf()
                val arrIndex = getIndex(level, index)
                val child = currNewNodes[arrIndex]
                if (child === null) {
                    currNewNode.nodes = currNewNodes
                    return retNode
                }
                // nextNewNodeをcurrNewNodeのChildrenに追加し, currNewNodeをnextNewNodeに差し替える.
                val nextNewNode = Node<T>(null, null)
                currNewNodes[arrIndex] = nextNewNode
                currNewNode.nodes = currNewNodes
                currNewNode = nextNewNode

                currNode = child
                level -= WIDTH
            }

            val newLeaves = currNode.leaves!!.copyOf()
            currNewNode.leaves = newLeaves

            return retNode
        }
    }
}
