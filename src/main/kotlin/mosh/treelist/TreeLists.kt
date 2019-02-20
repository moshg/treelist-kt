package mosh.treelist

internal const val B: Int = 32
internal const val WIDTH: Int = 5
internal const val MASK: Int = (1 shl WIDTH) - 1

private val EMPTY: TreeList<*> = TreeList<Any?>()

@Suppress("UNCHECKED_CAST")
fun <T> emptyTreeList(): TreeList<T> =
    EMPTY as TreeList<T>

fun <T> treeListOf(vararg elements: T): TreeList<T> = buildTreeList {
    for (e in elements) {
        add(e)
    }
}

fun <T> buildTreeList(block: TreeListBuilder<T>.() -> Unit): TreeList<T> {
    val builder = TreeListBuilder<T>()
    builder.block()
    return builder.build()
}
