import net.ruben.sdl.*

fun main() {
    SdlLog.setLogCallback { userData, category, priority, message ->
        println("$message")
    }
    if (!Sdl.init(SDL_INIT_VIDEO)) {
        SDL_Log("Couldn't initialize SDL: %s", SDL_GetError());
        return;
    }

    val window = SdlWindow.create("SDL3", 800, 600, 0UL)
    if (window == null) {
        SDL_Log("Couldn't create SDL Window: %s", SDL_GetError());
        return;
    }

    var renderer = SDLRenderer.create(window)
    if (renderer == null) {
        SDL_Log("Couldn't create SDL Renderer: %s", SDL_GetError());
        return;
    }

    var texture = SdlTexture.create(renderer, "textures/slime_walk1.png")
    if (texture == null) {
        return
    }

    var isRunning = true
    while (isRunning) {
        Sdl.pollEvent { event ->
            when (event.type) {
                SDL_EVENT_QUIT -> {
                    isRunning = false
                }
                SDL_EVENT_KEY_DOWN -> {
                    if (event.key.key == SDLK_ESCAPE) {
                        isRunning = false
                    }
                }
            }
        }

        val now = Sdl.getTicks().toFloat() / 1000.0f;
        val red = (0.5 + 0.5 * SDL_sin(now.toDouble()));
        val green = (0.5 + 0.5 * SDL_sin(now + SDL_PI_D * 2 / 3));
        val blue = (0.5 + 0.5 * SDL_sin(now + SDL_PI_D * 4 / 3));
        renderer.setDrawColor(red, green, blue, SDL_ALPHA_OPAQUE_FLOAT)
        renderer.clear()

        val dstRect = fRect(
            (SDL_sin(now.toDouble()).toFloat() * 30f) + 100,
            (SDL_cos(now.toDouble()).toFloat() * 30f) + 100,
            texture.width,
            texture.height
        )

        renderer.drawTexture(texture, null, dstRect)
        renderer.present()
    }

    texture.destroy()
    renderer.destroy()
    window.destroy()
    Sdl.quit()
}