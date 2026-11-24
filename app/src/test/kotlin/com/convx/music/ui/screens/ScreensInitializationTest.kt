package com.convx.music.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreensInitializationTest {
    @Test
    fun eachDestinationCanInitializeFirstInAFreshClassLoader() {
        val parent = javaClass.classLoader!!
        for ((destination, route) in listOf("Home" to "home", "Library" to "library", "Songs" to "songs", "Explore" to "explore", "Search" to "search_input", "Settings" to "settings")) {
            val isolated = object : ClassLoader(parent) {
                override fun loadClass(name: String, resolve: Boolean): Class<*> {
                    if (!name.startsWith("com.convx.music.ui.screens.Screens")) return super.loadClass(name, resolve)
                    synchronized(this) {
                        val loaded = findLoadedClass(name) ?: parent.getResourceAsStream(name.replace('.', '/') + ".class")!!.use {
                            val bytes = it.readBytes()
                            defineClass(name, bytes, 0, bytes.size)
                        }
                        if (resolve) resolveClass(loaded)
                        return loaded
                    }
                }
            }
            val baseName = "com.convx.music.ui.screens.Screens"
            val type = Class.forName(baseName + "$" + destination, true, isolated)
            val instance = type.getField("INSTANCE").get(null)
            assertEquals(route, type.getMethod("getRoute").invoke(instance))
            val base = Class.forName(baseName, true, isolated)
            val companion = base.getField("Companion").get(null)
            val mainScreens = companion.javaClass.getMethod("getMainScreens").invoke(companion) as List<*>
            assertEquals(listOf("home", "search_input", "radio", "library"), mainScreens.map { base.getMethod("getRoute").invoke(it) })
            assertEquals(setOf("home", "search_input", "radio", "library", "songs"), companion.javaClass.getMethod("getMainRoutes").invoke(companion))
        }
    }

    @Test
    fun destinationCanInitializeBeforeTheNavigationRegistry() {
        // This is the access order in MainActivityKt's top-level tab routes.
        // Eager companion collections capture Home.INSTANCE before assignment.
        assertEquals("home", Screens.Home.route)
        assertEquals(
            listOf("home", "search_input", "radio", "library"),
            Screens.MainScreens.map { it.route },
        )
        assertEquals(
            setOf("home", "search_input", "radio", "library", "songs"),
            Screens.MainRoutes,
        )
        assertEquals(
            listOf("home", "songs", "search_input", "library"),
            Screens.mainScreens(localOnly = true).map { it.route },
        )
    }
}
