package lambdastar314.simworld.util

class MergedList<T>(private val front: List<T>, private val back: List<T>) : List<T> {
    override val size: Int
        get() = front.size + back.size

    override fun contains(element: T): Boolean {
        return front.contains(element) || back.contains(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return front.containsAll(elements) || back.containsAll(elements)
    }

    override fun get(index: Int): T {
        return if (index < front.size) front[index] else back[index - front.size]
    }

    override fun indexOf(element: T): Int {
        val frontis = front.indexOf(element)
        return if (frontis != -1) frontis
        else back.indexOf(element) + front.size
    }

    override fun isEmpty(): Boolean {
        return front.isEmpty() || back.isEmpty()
    }

    /*
     *以下使う予定なしなので作る予定なし。
     */
    override fun iterator(): Iterator<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun lastIndexOf(element: T): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listIterator(): ListIterator<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listIterator(index: Int): ListIterator<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}