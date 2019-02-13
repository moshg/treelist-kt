import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class NodeTest {
    @Test
    fun get() {
        val leaves = arrayOfNulls<Any?>(Node.B).also {
            it[0] = 0
            it[1] = 2
        }
        val node = Node<Int>(null, leaves)
        assertEquals(2, node.get(0, 1))
        assertEquals(leaves, node.getLeaves(0, 1))

        val node2 = Node(arrayOfNulls<Node<Int>?>(Node.B).also { nodes ->
            nodes[0] = Node<Int>(null, Array(Node.B) { it })
            nodes[1] = node
        }, null)
        assertEquals(2, node2.get(Node.WIDTH, Node.B + 1))
        assertEquals(leaves, node2.getLeaves(Node.WIDTH, Node.B + 1))
    }

    @Test
    fun createSingle() {
        val node = Node.createSingle(Node.WIDTH, 2)
        assertEquals(null, node.leaves)

        for (i in 1 until Node.B) {
            assertEquals(null, node.nodes!![i])
        }
        val child = node.nodes!![0]!!
        assertEquals(null, child.nodes)
        assertEquals(2, child.leaves!![0])
    }

    @Test
    fun added() {
        val node = Node<Int>(null, arrayOfNulls(Node.B))
            .added(0, 0, 1)
            .added(0, 1, 2)
        assertEquals(1, node.get(0, 0))
        assertEquals(2, node.get(0, 1))

        val node2 = Node(arrayOfNulls<Node<Int>?>(Node.B).also { it[0] = node }, null)
            .added(Node.WIDTH, Node.B, 100)
        assertEquals(100, node2.get(Node.WIDTH, Node.B))
    }

    @Test
    fun set() {
        val node = Node<Int>(null, Array(Node.B) { it })
            .set(0, Node.B - 1, 100)
        assertEquals(100, node.get(0, Node.B - 1))

        val node2 = Node(arrayOfNulls<Node<Int>>(Node.B).also { it[0] = node }, null)
            .set(Node.WIDTH, 1, 12)
        assertEquals(12, node2.get(Node.WIDTH, 1))
    }
}
