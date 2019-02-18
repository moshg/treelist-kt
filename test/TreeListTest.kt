import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TreeListTest {
    @Test
    fun get() {
        assertThrows<IndexOutOfBoundsException> { TreeList<Int>()[0] }
    }

    @Test
    fun getSize() {
        assertEquals(0, TreeList<Int>().size)
    }

    @Test
    fun added() {
        val l = TreeList<Int>()
            .added(5).added(6)
        assertEquals(2, l.size)
        assertEquals(5, l[0])
        assertEquals(6, l[1])

        var l2 = TreeList<Int>()
        for (i in 0 until B) {
            l2 = l2.added(i + 2)
        }
        var l3 = l2
        for (i in B until B * B + 1) {
            l3 = l3.added(i + 2)
        }

        assertEquals(B, l2.size)
        for (i in 0 until B) {
            assertEquals(i + 2, l2[i])
        }
        assertEquals(B * B + 1, l3.size)
        for (i in 0 until B * B + 1) {
            assertEquals(i + 2, l3[i])
        }
    }

    @Test
    fun set() {
        assertThrows<IndexOutOfBoundsException> { TreeList<Int>().set(0, 0) }

        val l = treeListOf(0, 1).set(0, 2)
        assertEquals(2, l[0])
    }

    @Test
    fun contains() {
        assert(!TreeList<Int>().contains(0))

        val l = treeListOf(10, 20, 30)
        assert(l.contains(20))
        assert(!l.contains(50))
    }

    @Test
    fun containsAll() {
        assert(TreeList<Int>().containsAll(ArrayList()))

        val l = treeListOf(10, 20, 30)
        assert(l.containsAll(arrayListOf(20, 30)))
        assert(!l.containsAll(arrayListOf(30, 40)))
    }

    @Test
    fun indexOf() {
        assertEquals(-1, TreeList<Int>().indexOf(0))
        assertEquals(-1, treeListOf(0, 1, 2).indexOf(3))
        assertEquals(1, treeListOf(0, 2, 4, 6, 2).indexOf(2))
    }

    @Test
    fun lastIndexOf() {
        assertEquals(-1, TreeList<Int>().lastIndexOf(0))
        assertEquals(-1, treeListOf(0, 1, 2).lastIndexOf(3))
        assertEquals(4, treeListOf(0, 2, 4, 6, 2, 0).lastIndexOf(2))
    }

    @Test
    operator fun iterator() {
        val emptyIter = treeListOf<Int>().iterator()
        assertEquals(false, emptyIter.hasNext())
        assertThrows<NoSuchElementException> { emptyIter.next() }

        var l = treeListOf<Int>()
        for (i in 0 until B * B + 1) {
            l = l.added(i + 3)
        }
        var i = 0
        val iter = l.iterator()
        for (e in iter) {
            assertEquals(i + 3, e)
            i += 1
        }
        assertThrows<NoSuchElementException> { iter.next() }
    }

    @Test
    fun listIterator() {
        val emptyIter = treeListOf<Int>().listIterator()
        assertEquals(false, emptyIter.hasNext())
        assertEquals(false, emptyIter.hasPrevious())
        assertThrows<NoSuchElementException> { emptyIter.next() }
        assertThrows<NoSuchElementException> { emptyIter.previous() }

        var l = treeListOf<Int>()
        for (i in 0 until B * B + 1) {
            l = l.added(i + 3)
        }
        var i = 0
        val iter = l.listIterator()
        while (iter.hasNext()) {
            assertEquals(i, iter.nextIndex())
            assertEquals(i + 3, iter.next())
            i += 1
        }
        assertThrows<NoSuchElementException> { iter.next() }

        i = B * B + 1 - 1
        val iterRev = l.listIterator(l.size)
        while (iterRev.hasPrevious()) {
            assertEquals(i, iterRev.previousIndex())
            assertEquals(i + 3, iterRev.previous())
            i -= 1
        }
        assertThrows<NoSuchElementException> { iterRev.previous() }
    }
}
