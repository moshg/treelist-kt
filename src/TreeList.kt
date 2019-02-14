class TreeList<T> internal constructor(private var root: Node<T>?, private val level: Int, override val size: Int) :
    List<T> {
    constructor() : this(null, -Node.WIDTH, 0)

    override operator fun get(index: Int): T {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException(index)
        }
        return root!!.get(level, index)
    }

    fun added(e: T): TreeList<T> {
        val size = size
        if (size == Int.MAX_VALUE) {
            throw IllegalStateException("Size exceeds the max value of integer")
        }

        val level = level
        return if (size == 1 shl (level + Node.WIDTH)) {
            // 全ノードが埋まっていて root != null の場合
            val nodes = arrayOfNulls<Node<T>>(Node.B).also {
                it[0] = root
                it[1] = Node.createSingle(level, e)
            }
            TreeList(Node(nodes, null), level + Node.WIDTH, size + 1)
        } else {
            val root = root
            if (root == null) {
                TreeList(Node.createSingle(0, e), 0, 1)
            } else {
                TreeList(root.added(this.level, size, e), level, size + 1)
            }
        }
    }

    fun set(index: Int, e: T): TreeList<T> {
        if (index < 0) {
            throw IndexOutOfBoundsException(index)
        } else if (index >= size) {
            throw IndexOutOfBoundsException("Index $index out of bounds for size $size")
        }
        return TreeList(root!!.set(level, index, e), level, size)
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
        var index = 0
        for (e in this) {
            if (element == e) {
                return index
            }
            index += 1
        }
        return -1
    }

    override fun lastIndexOf(element: T): Int {
        var index = size - 1
        val iter = listIterator(size)
        while (iter.hasPrevious()) {
            if (element == iter.previous()) {
                return index
            }
            index -= 1
        }
        return -1
    }

    override fun iterator(): Iter<T> = Iter(root, level, size, 0, null)

    override fun listIterator(): ListIter<T> = ListIter(root, level, size, 0, null)

    override fun listIterator(index: Int): ListIter<T> {
        if (index < 0 || index > size) {
            throw IndexOutOfBoundsException(index)
        }

        val root = root
        return if (root === null) {
            ListIter(root, level, size, index, null)
        } else {
            if (index == size) {
                ListIter(root, level, size, index, root.getLeaves(level, index - 1))
            } else {
                ListIter(root, level, size, index, root.getLeaves(level, index))
            }
        }
    }

    class Iter<T> internal constructor(
        private val root: Node<T>?,
        private val level: Int,
        private val size: Int,
        private var index: Int,
        private var leaves: Array<Any?>?
    ) : Iterator<T> {
        override fun hasNext(): Boolean = index < size

        override fun next(): T {
            if (index >= size) {
                throw NoSuchElementException("Index $index out of bounds for size $size")
            }

            val masked = index and Node.MASK
            if (masked == 0) {
                leaves = root!!.getLeaves(level, index)
                index += 1
                @Suppress("UNCHECKED_CAST")
                return leaves!![0] as T
            } else {
                val e = leaves!![masked]
                index += 1
                @Suppress("UNCHECKED_CAST")
                return e as T
            }
        }
    }

    class ListIter<T> internal constructor(
        private val root: Node<T>?,
        private val level: Int,
        private val size: Int,
        private var index: Int,
        private var leaves: Array<Any?>?
    ) : ListIterator<T> {
        override fun hasNext(): Boolean = index < size

        override fun next(): T {
            if (index >= size) {
                throw NoSuchElementException("Index $index out of bounds for size $size")
            }

            val masked = index and Node.MASK
            if (masked == 0) {
                leaves = root!!.getLeaves(level, index)
                index += 1
                @Suppress("UNCHECKED_CAST")
                return leaves!![0] as T
            } else {
                val e = leaves!![masked]
                index += 1
                @Suppress("UNCHECKED_CAST")
                return e as T
            }
        }

        override fun nextIndex(): Int = index

        override fun hasPrevious(): Boolean = index > 0

        override fun previous(): T {
            if (index <= 0) {
                throw NoSuchElementException("Index $index out of bounds")
            }

            index -= 1
            val masked = index and Node.MASK
            if (masked == Node.MASK) {
                leaves = root!!.getLeaves(level, index)
                @Suppress("UNCHECKED_CAST")
                return leaves!![Node.MASK] as T
            } else {
                @Suppress("UNCHECKED_CAST")
                return leaves!![masked] as T
            }
        }

        override fun previousIndex(): Int = index - 1
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        if (fromIndex > toIndex) {
            throw IllegalArgumentException("fromIndex $fromIndex greater than toIndex $toIndex")
        } else if (fromIndex < 0) {
            throw IndexOutOfBoundsException("fromIndex $fromIndex out of bounds")
        } else if (toIndex > size) {
            throw IndexOutOfBoundsException("toIndex $toIndex out of bounds for size $size")
        }
        return SubTreeList(root, level, fromIndex, toIndex)
    }

    override fun toString(): String {
        if (size == 0) {
            return "[]"
        }

        val i = iterator()
        return buildString(size * 2) {
            append('[')
            append(i.next())
            for (e in i) {
                append(", ")
                append(e)
            }
            append(']')
        }
    }
}
