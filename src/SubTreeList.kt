class SubTreeList<T> internal constructor(
    private val root: Node<T>?, private val level: Int, val start: Int, val end: Int
) : List<T> {
    override val size: Int
        get() = end - start

    override operator fun get(index: Int): T {
        val i = index + start
        if (index < 0 || i >= start) {
            throw IndexOutOfBoundsException(index)
        }
        return root!!.get(level, i)
    }

    override fun isEmpty(): Boolean = start == end

    override fun contains(element: T): Boolean {
        for (e in this) {
            if (element == e) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        val set = this.toSet()
        return set.containsAll(elements)
    }

    override fun indexOf(element: T): Int {
        var index = start
        for (e in this) {
            if (element == e) {
                return index - start
            }
            index += 1
        }
        return -1
    }

    override fun lastIndexOf(element: T): Int {
        var index = end - 1
        val iter = listIterator(size - 1)
        while (iter.hasPrevious()) {
            if (element == iter.previous()) {
                return index - start
            }
            index -= 1
        }
        return -1
    }

    override fun iterator(): Iter<T> = Iter(root, level, start, end, 0, null)

    override fun listIterator(): ListIter<T> = ListIter(root, level, start, end, 0, null)

    override fun listIterator(index: Int): ListIter<T> {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException(index)
        }
        return ListIter(root, level, start, end, index, null)
    }

    override fun subList(fromIndex: Int, toIndex: Int): SubTreeList<T> {
        if (fromIndex > toIndex) {
            throw IllegalArgumentException("fromIndex $fromIndex greater than toIndex $toIndex")
        } else if (fromIndex < 0) {
            throw IndexOutOfBoundsException("fromIndex $fromIndex out of bounds")
        } else if (toIndex > size) {
            throw IndexOutOfBoundsException("toIndex $toIndex out of bounds for size $size")
        }
        return SubTreeList(root, level, fromIndex + start, toIndex + start)
    }

    class Iter<T> internal constructor(
        private val root: Node<T>?,
        private val level: Int,
        private val start: Int,
        private val end: Int,
        private var index: Int,
        private var leaves: Array<Any?>?
    ) : Iterator<T> {
        override fun hasNext(): Boolean = index < end

        override fun next(): T {
            if (index >= end) {
                throw NoSuchElementException("Index ${index - start} out of bounds for size ${end - start}")
            }

            if (index and Node.MASK == 0) {
                leaves = root!!.getLeaves(level, index)
                index += 1
                @Suppress("UNCHECKED_CAST")
                return leaves!![0] as T
            } else {
                val e = leaves!![index]
                index += 1
                @Suppress("UNCHECKED_CAST")
                return e as T
            }
        }
    }

    class ListIter<T> internal constructor(
        private val root: Node<T>?,
        private val level: Int,
        private val start: Int,
        private val end: Int,
        private var index: Int,
        private var leaves: Array<Any?>?
    ) : ListIterator<T> {
        override fun hasNext(): Boolean = index < end

        override fun next(): T {
            if (index >= end) {
                throw NoSuchElementException("Index ${index - start} out of bounds for size ${end - start}")
            }

            if (index and Node.MASK == 0) {
                leaves = root!!.getLeaves(level, index)
                index += 1
                @Suppress("UNCHECKED_CAST")
                return leaves!![0] as T
            } else {
                val e = leaves!![index]
                index += 1
                @Suppress("UNCHECKED_CAST")
                return e as T
            }
        }

        override fun nextIndex(): Int = index

        override fun hasPrevious(): Boolean = index > 0

        override fun previous(): T {
            if (index <= start) {
                throw NoSuchElementException("Index ${index - start} out of bounds")
            }

            index -= 1

            if (index and Node.MASK == Node.MASK) {
                leaves = root!!.getLeaves(level, index)
                @Suppress("UNCHECKED_CAST")
                return leaves!![Node.MASK] as T
            } else {
                @Suppress("UNCHECKED_CAST")
                return leaves!![index] as T
            }
        }

        override fun previousIndex(): Int = index - 1
    }
}
