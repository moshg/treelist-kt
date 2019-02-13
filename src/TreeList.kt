class TreeList<T> private constructor(override val size: Int, private val level: Int, private val root: Node<T>) :
    List<T> {
    override operator fun get(index: Int): T {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException(index)
        }
        return root.get(index, level)
    }

    fun added(e: T): TreeList<T> {
        val size = size
        if (size == Int.MAX_VALUE) {
            throw IllegalStateException("Size exceeds the max value of integer")
        }

        val level = level
        return if (size == 1 shl (level + Node.WIDTH)) {
            // 全ノードが埋まっている場合
            val nodes = arrayOfNulls<Node<T>>(Node.B)
            nodes[0] = root
            nodes[1] = Node.createSingle(level, e)
            TreeList(size + 1, level + Node.WIDTH, Node(nodes, null))
        } else {
            TreeList(size + 1, level, root.added(size, this.level, e))
        }
    }

    override fun isEmpty(): Boolean = size == 0

    override fun contains(element: T): Boolean {
        for (e in this) {
            if (element == e) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        val set = this.toHashSet()
        return set.containsAll(elements)
    }

    override fun indexOf(element: T): Int {
        val level = level
        for (i in 0 until size) {
            if (element == this.root.get(i, level)) {
                return i
            }
        }
        return -1
    }

    override fun lastIndexOf(element: T): Int {
        val level = level
        for (i in (size - 1) downTo 0) {
            if (element == this.root.get(i, level)) {
                return i
            }
        }
        return -1
    }

    override fun iterator(): ListIter<T> = ListIter(this, 0)

    override fun listIterator(): ListIter<T> = ListIter(this, 0)

    override fun listIterator(index: Int): ListIter<T> = ListIter(this, index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        TODO("not implemented")
    }

    class ListIter<T> internal constructor(private val list: TreeList<T>, private var index: Int) : ListIterator<T> {
        override fun hasNext(): Boolean = index < list.size

        override fun hasPrevious(): Boolean = index >= 1

        override fun next(): T {
            if (index >= list.size) {
                throw NoSuchElementException("Index $index out of bounds for size ${list.size}")
            }

            val e = list.root.get(index, list.level)
            index += 1
            return e
        }

        override fun nextIndex(): Int = index

        override fun previous(): T {
            if (index <= 0) {
                throw NoSuchElementException("Index $index out of bounds")
            }

            index -= 1
            return list.root.get(index, list.level)
        }

        override fun previousIndex(): Int = index - 1
    }
}
