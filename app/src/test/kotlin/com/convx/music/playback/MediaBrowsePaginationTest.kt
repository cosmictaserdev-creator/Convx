package com.convx.music.playback

import org.junit.Assert.assertEquals
import org.junit.Test

class MediaBrowsePaginationTest {
    private val items = (1..10).toList()

    @Test
    fun returnsRequestedPage() {
        assertEquals(listOf(4, 5, 6), items.paginate(page = 1, pageSize = 3))
    }

    @Test
    fun lastPageIsPartial() {
        assertEquals(listOf(10), items.paginate(page = 3, pageSize = 3))
    }

    @Test
    fun pageBeyondEndIsEmpty() {
        assertEquals(emptyList<Int>(), items.paginate(page = 4, pageSize = 3))
    }

    @Test
    fun unboundedPageSizeReturnsEverything() {
        assertEquals(items, items.paginate(page = 0, pageSize = Int.MAX_VALUE))
    }

    @Test
    fun secondUnboundedPageIsEmpty() {
        assertEquals(emptyList<Int>(), items.paginate(page = 1, pageSize = Int.MAX_VALUE))
    }

    @Test
    fun invalidArgumentsReturnEmpty() {
        assertEquals(emptyList<Int>(), items.paginate(page = -1, pageSize = 3))
        assertEquals(emptyList<Int>(), items.paginate(page = 0, pageSize = 0))
    }

    @Test
    fun largePageIndexDoesNotOverflow() {
        assertEquals(emptyList<Int>(), items.paginate(page = Int.MAX_VALUE, pageSize = 1_000))
    }
}
