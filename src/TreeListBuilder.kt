import Node.Companion.createSingleLeaves
import Node.Companion.getIndex
import TreeList.Companion.emptyNodes

class TreeListBuilder<T> internal constructor(
    private var level: Int,
    private var nodes: Array<Node<T>?>?,
    private var nodesLen: Int,
    private var tail: Array<Any?>?,
    private var tailLen: Int
) {
    constructor() : this(0, null, 0, null, 0)

    fun add(e: T) {
        val tailLen = this.tailLen
        val tail = this.tail
        if (tail === null) {
            this.tail = arrayOfNulls<Any?>(B).also { it[0] = e }
            this.tailLen = 1
        } else if (tailLen < tail.size) {
            // tailが空いている場合
            tail[tailLen] = e
            this.tailLen = tailLen + 1
        } else {
            this.tail = arrayOfNulls<Any?>(B).also { it[0] = e }
            this.tailLen = 1
            val nodes = this.nodes
            if (nodes === null) {
                // nodesが未初期化状態の場合
                this.level += WIDTH
                this.nodes = arrayOfNulls<Node<T>>(B).also { it[0] = Node(null, tail) }
                this.nodesLen = B
            } else if (nodesLen == 1 shl (level + WIDTH)) {
                // nodesが埋まっている場合
                this.level += WIDTH
                val oldRoot = Node(nodes, null)
                val second = Node.createSingleLeaves<T>(level, tail)
                this.nodes = arrayOfNulls<Node<T>>(B).also {
                    it[0] = oldRoot
                    it[1] = second
                }
                nodesLen += B
            } else {
                // nodesに空きがある場合
                val level = this.level
                val nodesLen = this.nodesLen
                this.nodesLen = nodesLen + B
                val index = getIndex(level, nodesLen)
                val node = nodes[index]
                if (node === null) {
                    nodes[index] = createSingleLeaves(level - WIDTH, tail)
                } else {
                    node.addLeaves(level - WIDTH, nodesLen, tail)
                }
            }
        }
    }

    fun build(): TreeList<T> {
        val tail = this.tail
        if (tail === null) {
            return TreeList()
        } else {
            val nodes = this.nodes
            if (nodes === null) {
                this.tail = null
                val tailLen = this.tailLen
                this.tailLen = 0
                return TreeList(0, emptyNodes(), 0, tail, tailLen)
            } else {
                val level = this.level
                this.level = 0
                this.nodes = null
                val nodesLen = this.nodesLen
                this.nodesLen = 0
                this.tail = null
                val tailLen = tailLen
                this.tailLen = 0
                return TreeList(level, nodes, nodesLen, tail, tailLen)
            }
        }
    }
}
