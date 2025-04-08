package net.ruben.sdl

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ptr
import net.ruben.c.graphics.*

class SDLRenderer {
    private constructor()

    var raw: CPointer<cnames.structs.SDL_Renderer>? = null
        private set

    fun destroy() {
        assert(raw != null)
        SDL_DestroyRenderer(raw)
    }

    fun clear() {
        assert(raw != null)
        SDL_RenderClear(raw)
    }

    fun present() {
        assert(raw != null)
        SDL_RenderPresent(raw);
    }

    fun setDrawColor(red: Float, green: Float, blue: Float, alpha: Float) {
        assert(raw != null)
        SDL_SetRenderDrawColorFloat(
            raw,
            red,
            green,
            blue,
            alpha
        );
    }

    fun setDrawColor(red: Double, green: Double, blue: Double, alpha: Float) =
        setDrawColor(red.toFloat(), green.toFloat(), blue.toFloat(), alpha)

    fun drawTexture(texture: SdlTexture, srcRect: SDL_FRect? = null, destRect: SDL_FRect? = null) {
        assert(raw != null)
        SDL_RenderTexture(raw, texture.raw, srcRect?.ptr, destRect?.ptr)
    }

    companion object {
        fun create(window: SdlWindow, name: String? = null): SDLRenderer? {
            var result = SDLRenderer()
            result.raw = SDL_CreateRenderer(window.raw, name)
            if (result.raw == null) {
                SDL_Log("Couldn't create SDL Renderer: %s", SDL_GetError());
                return null
            }
            return result
        }
    }
}

