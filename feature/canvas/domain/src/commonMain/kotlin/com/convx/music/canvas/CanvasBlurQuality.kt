package com.convx.music.canvas

/** Spatial resolution changes while blur strength and video timing remain constant. */
enum class CanvasBlurQuality(val downsample: Int) {
    HIGH(2), BALANCED(3), LOW(4);
}
