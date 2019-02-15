@Suppress("NAME_SHADOWING")
internal class Node<T>(var nodes: Array<Node<T>?>?, var leaves: Array<Any?>?) {
    @Suppress("NOTHING_TO_INLINE")
    inline fun get(level: Int, index: Int): T = get(this, level, index)

    @Suppress("NOTHING_TO_INLINE")
    inline fun getLeaves(level: Int, index: Int): Array<Any?> = getLeaves(this, level, index)

    @Suppress("NOTHING_TO_INLINE")
    inline fun added(level: Int, index: Int, e: T): Node<T> = added(this, level, index, e)

    @Suppress("NOTHING_TO_INLINE")
    inline fun addedLeaves(level: Int, index: Int, leaves: Array<Any?>) = addedLeaves(this, level, index, leaves)

    @Suppress("NOTHING_TO_INLINE")
    inline fun set(level: Int, index: Int, e: T): Node<T> = set(this, level, index, e)

    override fun toString(): String = buildString(B * 3) {
        if (leaves === null) {
            append("(")
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
            append("(")
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
        @JvmStatic
        inline fun getIndex(level: Int, i: Int): Int = (i ushr level) and MASK

        @JvmStatic
        fun <T> createSingle(level: Int, e: T): Node<T> =
            createSingleLeaves(level, arrayOfNulls<Any?>(B).also { it[0] = e })

        @JvmStatic
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

        @JvmStatic
        tailrec fun <T> get(node: Node<T>, level: Int, index: Int): T {
            return if (level == 0) {
                @Suppress("UNCHECKED_CAST")
                node.leaves!![index and MASK] as T
            } else {
                get(node.nodes!![getIndex(level, index)]!!, level - WIDTH, index)
            }
        }

        @JvmStatic
        tailrec fun <T> getLeaves(node: Node<T>, level: Int, index: Int): Array<Any?> {
            return if (level == 0) {
                node.leaves!!
            } else {
                getLeaves(node.nodes!![getIndex(level, index)]!!, level - WIDTH, index)
            }
        }

        @JvmStatic
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

        @JvmStatic
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
                    currNewNodes[arrIndex] = createSingleLeaves(level - WIDTH, leaves)
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

        @JvmStatic
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
    }
}
