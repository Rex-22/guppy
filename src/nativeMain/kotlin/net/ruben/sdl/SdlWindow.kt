package net.ruben.sdl

import kotlinx.cinterop.CPointer
import net.ruben.c.graphics.*

class SdlWindow {
    private constructor()

    var raw: CPointer<cnames.structs.SDL_Window>? = null
        private set

    fun destroy() {
        assert(raw != null)
        SDL_DestroyWindow(raw)
    }

    companion object {
        fun create(title: String, width: Int, height: Int, flags: SDL_WindowFlags = 0UL): SdlWindow? {
            var result = SdlWindow()
            result.raw = SDL_CreateWindow(title, width, height, flags)
            if (result.raw == null) {
                return null
            }
            return result
        }
    }
}
