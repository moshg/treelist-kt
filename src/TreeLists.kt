internal const val B: Int = 32
internal const val WIDTH: Int = 5
internal const val MASK: Int = (1 shl WIDTH) - 1

fun <T> treeListOf(vararg elements: T): TreeList<T> {
    var l = TreeList<T>()
    for (e in elements) {
        l = l.added(e)
    }
    return l
}

fun <T> buildTreeList(block: TreeListBuilder<T>.() -> Unit): TreeList<T> {
    val builder = TreeListBuilder<T>()
    builder.block()
    return builder.build()
}
