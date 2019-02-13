import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class NodeTest {
    @Test
    fun get() {
        val leaves = arrayOfNulls<Any?>(Node.B)
        leaves[0] = 0
        leaves[1] = 2
        val node = Node<Int>(null, leaves)
        assertEquals(2, node.get(0, 1))
        assertEquals(leaves, node.getLeaves(0, 1))

        val nodes = arrayOfNulls<Node<Int>?>(Node.B)
        nodes[0] = Node<Int>(null, Array(Node.B) { it })
        nodes[1] = node
        val root = Node(nodes, null)
        assertEquals(2, root.get(Node.WIDTH, Node.B + 1))
        assertEquals(leaves, root.getLeaves(Node.WIDTH, Node.B + 1))
    }

    @Test
    fun createSingle() {
        val node = Node.createSingle(Node.WIDTH, 2)
        assertEquals(null, node.leaves)
        val child = node.nodes!![0]!!
        for (i in 1 until Node.B) {
            assertEquals(null, node.nodes!![i])
        }
        assertEquals(null, child.nodes)
        assertEquals(2, child.leaves!![0])
    }
}
