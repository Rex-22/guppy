package net.ruben.sdl

import kotlinx.cinterop.*

class SdlTexture {
    private constructor()

    val width: Float
        get() {
            val (w, _) = getSize()
            return w;
        }

    val height: Float
        get() {
            val (_, h) = getSize()
            return h;
        }

    val format: UInt
        get() {
            assert(raw != null)
            return raw!!.pointed.format
        }

    var raw: CPointer<SDL_Texture>? = null
        private set

    fun getSize(): Pair<Float, Float> {
        assert(raw != null)
        memScoped {
            val tw = alloc<FloatVar>()
            val th = alloc<FloatVar>()

            SDL_GetTextureSize(raw, tw.ptr, th.ptr)

            val result = Pair(tw.value, th.value)

            return result
        }
    }

    fun destroy() {
        assert(raw != null)
        SDL_DestroyTexture(raw)
    }

    companion object {
        fun create(renderer: SDLRenderer, file: String): SdlTexture? {
            val result = SdlTexture()
            result.raw = IMG_LoadTexture(renderer.raw, file)
            if (result.raw == null) {
                SDL_Log("Couldn't load texture: %s", SDL_GetError());
                return null
            }
            return result;
        }
    }
}

