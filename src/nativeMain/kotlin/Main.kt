import kotlinx.cinterop.*
import net.ruben.sdl.*
import platform.posix.getcwd
import platform.windows.MAX_PATH

data class Bunny @OptIn(ExperimentalForeignApi::class) constructor(
    val positionX: Float,
    val positionY: Float,
    val speedX: Float,
    val speedY: Float,
    val color: SDL_Color
)

@OptIn(ExperimentalForeignApi::class)
fun main() {
    val workDir = nativeHeap.allocArray<ByteVar>(MAX_PATH)
    getcwd(workDir, MAX_PATH)
    SdlLog.setLogCallback { userData, category, priority, message ->
        println("$message")
    }
    if (!SDL_Init(SDL_INIT_VIDEO)) {
        SDL_Log("Couldn't initialize SDL: %s", SDL_GetError());
        return;
    }

    val window = SDL_CreateWindow("SDL3", 800, 600, 0UL)
    if (window == null) {
        SDL_Log("Couldn't create SDL Window: %s", SDL_GetError());
    }

    var renderer = SDL_CreateRenderer(window, null)
    if (renderer == null) {
        SDL_Log("Couldn't create SDL Renderer: %s", SDL_GetError());
    }

    var texture = IMG_LoadTexture(renderer, "textures/slime_walk1.png")
    if (texture == null) {
        SDL_Log("Couldn't load texture: %s", SDL_GetError());
    }
    var isRunning = true
    while (isRunning) {
        memScoped {
            val event = alloc<SDL_Event>()
            while (SDL_PollEvent(event.ptr)) {
                if (event.type == SDL_EVENT_QUIT) {
                    isRunning = false
                }
            }
        }

        val now = SDL_GetTicks().toFloat() / 1000.0f;  /* convert from milliseconds to seconds. */
        /* choose the color for the frame we will draw. The sine wave trick makes it fade between colors smoothly. */
        val red = (0.5 + 0.5 * SDL_sin(now.toDouble()));
        val green = (0.5 + 0.5 * SDL_sin(now + SDL_PI_D * 2 / 3));
        val blue = (0.5 + 0.5 * SDL_sin(now + SDL_PI_D * 4 / 3));
        SDL_SetRenderDrawColorFloat(
            renderer,
            red.toFloat(),
            green.toFloat(),
            blue.toFloat(),
            SDL_ALPHA_OPAQUE_FLOAT
        );  /* new color, full alpha. */
        SDL_RenderClear(renderer);

        memScoped {
            val dstRect = alloc<SDL_FRect>()
            val tw = alloc<FloatVar>()
            val th = alloc<FloatVar>()
            SDL_GetTextureSize(texture, tw.ptr, th.ptr)

            dstRect.x =  (SDL_sin(now.toDouble()).toFloat() * 30f)+100
            dstRect.y = (SDL_cos(now.toDouble()).toFloat() * 30f)+100
            dstRect.w = tw.value
            dstRect.h = th.value

            SDL_RenderTexture(renderer, texture, null, dstRect.ptr)
        }
        SDL_RenderPresent(renderer);

    }
    nativeHeap.free(workDir)
    SDL_DestroyTexture(texture)
    SDL_DestroyRenderer(renderer)
    SDL_DestroyWindow(window)
    SDL_Quit()
}