internal class Node<T>(var children: Array<Node<T>?>?, var leaves: Array<Any?>?) {
    @Suppress("NOTHING_TO_INLINE")
    inline fun get(index: Int, level: Int): T = get(this, index, level)

    @Suppress("NOTHING_TO_INLINE")
    inline fun added(index: Int, level: Int, e: T): Node<T> = added(this, index, level, e)

    companion object {
        const val B: Int = 32
        const val WIDTH: Int = 5
        const val MASK: Int = (1 shl WIDTH) - 1

        @JvmStatic
        fun <T> createSingle(level: Int, e: T): Node<T> {
            var level = level
            val elements = arrayOfNulls<Any?>(Node.B)
            elements[0] = e
            var node = Node<T>(null, elements)
            while (level > 0) {
                val nodes = arrayOfNulls<Node<T>>(Node.B)
                nodes[0] = node
                node = Node(nodes, null)
                level -= 5
            }
            return node
        }

        @JvmStatic
        tailrec fun <T> get(node: Node<T>, index: Int, level: Int): T {
            return if (level == 0) {
                @Suppress("UNCHECKED_CAST")
                node.leaves!![index and MASK] as T
            } else {
                get(node.children!![(index ushr level) and MASK]!!, index, level - WIDTH)
            }
        }

        @JvmStatic
        fun <T> added(node: Node<T>, index: Int, level: Int, e: T): Node<T> {
            var level = level
            val newNode = Node<T>(null, null)
            // 現在初期化しているノード
            var currNode = newNode

            while (level > 0) {
                val newChildren = node.children!!.copyOf()
                val arrIndex = (index ushr level) and MASK
                val child = newChildren[arrIndex]
                if (child === null) {
                    newChildren[arrIndex] = createSingle(level - WIDTH, e)
                    currNode.children = newChildren
                    break
                } else {
                    currNode.children = newChildren
                    currNode = child
                }
                level -= WIDTH
            }

            if (level == 0) {
                val newLeaves = arrayOfNulls<Any?>(WIDTH)
                newLeaves[index and MASK] = e
                currNode.leaves = newLeaves
            }

            return newNode
        }
    }
}
