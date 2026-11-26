package com.convx.music.canvas

data class CanvasBlurWorkload(val mediaId: String? = null, val changedAtMillis: Long? = null, val reduced: Boolean = false) {
    fun transition(nextId: String?, nowMillis: Long): CanvasBlurWorkload {
        if (nextId == mediaId) return this
        val rapid = nextId != null && mediaId != null && changedAtMillis?.let { nowMillis - it in 0..1_500 } == true
        return CanvasBlurWorkload(nextId, nowMillis, rapid)
    }
    fun settled(): CanvasBlurWorkload = copy(reduced = false)
}
