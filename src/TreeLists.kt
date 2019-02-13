fun <T> treeListOf(vararg elements: T): TreeList<T> {
    var l = TreeList<T>()
    for (e in elements) {
        l = l.added(e)
    }
    return l
}
