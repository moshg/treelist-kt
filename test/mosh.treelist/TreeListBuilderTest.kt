package mosh.treelist

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class TreeListBuilderTest {
    @Test
    fun build() {
        val b = TreeListBuilder<Int>()

        assert(b.build().isEmpty())

        b.add(2)
        b.add(3)
        val l = b.build()
        assertEquals(2, l[0])
        assertEquals(3, l[1])

        for (i in 0 until B * B * B + 1) {
            b.add(i + 5)
        }
        val l2 = b.build()
        assertEquals(2, l[0])
        assertEquals(3, l[1])
        for (i in 0 until B * B * B + 1) {
            assertEquals(i + 5, l2[i])
        }
    }
}
