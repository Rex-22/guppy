package net.ruben.sdl

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import net.ruben.c.graphics.*


object Sdl {
    fun init(flags: SDL_InitFlags): Boolean {
        return SDL_Init(flags)
    }

    fun getTicks(): ULong {
        return SDL_GetTicks()
    }

    fun quit() {
        SDL_Quit()
    }

    fun pollEvent(callback: (SDL_Event) -> Unit) {
        memScoped {
            val event = alloc<SDL_Event>()
            while (SDL_PollEvent(event.ptr)) {
                callback(event)
            }
        }
    }
}