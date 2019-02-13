class TreeList<T> private constructor(private val size: Int, private val level: Int, private val root: Node<T>) {
    fun get(index: Int): T {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException(index)
        }
        return root.get(index, level)
    }

    fun added(e: T): TreeList<T> {
        val size = size
        val level = level
        if (size == 1 shl (level + Node.WIDTH)) {
            // 全ノードが埋まっている場合
            val nodes = arrayOfNulls<Node<T>>(Node.B)
            nodes[0] = root
            nodes[1] = Node.createSingle(level, e)
            return TreeList(size + 1, level + Node.WIDTH, Node(nodes, null))
        } else {
            return TreeList(size + 1, level, root.added(size, this.level, e))
        }
    }
}