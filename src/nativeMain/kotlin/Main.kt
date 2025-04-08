import kotlinx.cinterop.*
import net.ruben.c.graphics.*
import net.ruben.sdl.Sdl
import net.ruben.sdl.SdlLog
import net.ruben.sdl.SdlWindow
import net.ruben.sdl.fRect

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
//
//    var renderer = SDLRenderer.create(window)
//    if (renderer == null) {
//        SDL_Log("Couldn't create SDL Renderer: %s", SDL_GetError());
//        return;
//    }

    // Create GPU Device
    val gpuDevice = SDL_CreateGPUDevice(
        SDL_GPU_SHADERFORMAT_SPIRV or SDL_GPU_SHADERFORMAT_DXIL or SDL_GPU_SHADERFORMAT_METALLIB,
        true,
        null
    );
    if (gpuDevice == null) {
        SDL_Log("Error: SDL_CreateGPUDevice(): %s", SDL_GetError());
        return
    }

    if (!SDL_ClaimWindowForGPUDevice(gpuDevice, window.raw)) {
        SDL_Log("Error: SDL_ClaimWindowForGPUDevice(): %s\n", SDL_GetError())
        return
    }
    SDL_SetGPUSwapchainParameters(
        gpuDevice,
        window.raw,
        SDL_GPUSwapchainComposition.SDL_GPU_SWAPCHAINCOMPOSITION_SDR,
        SDL_GPUPresentMode.SDL_GPU_PRESENTMODE_VSYNC
    );

//    var texture = SdlTexture.create(renderer, "textures/slime_walk1.png")
//    if (texture == null) {
//        return
//    }

    igCreateContext(null)
    val io = igGetIO_Nil()
    io!!.pointed.ConfigFlags = io.pointed.ConfigFlags or ImGuiConfigFlags_NavEnableKeyboard.toInt()
    io.pointed.ConfigFlags = io.pointed.ConfigFlags or ImGuiConfigFlags_NavEnableGamepad.toInt();

    igStyleColorsDark(null)

    ImGui_ImplSDL3_InitForSDLGPU(window.raw)
    memScoped {
        val info = alloc<ImGui_ImplSDLGPU3_InitInfo>()
        info.Device = gpuDevice
        info.ColorTargetFormat = SDL_GetGPUSwapchainTextureFormat(gpuDevice, window.raw)
        info.MSAASamples = SDL_GPUSampleCount.SDL_GPU_SAMPLECOUNT_1
        ImGui_ImplSDLGPU3_Init(info.ptr)
    }


    var show_demo_window = nativeHeap.alloc(true)
    var isRunning = true
    while (isRunning) {
        Sdl.pollEvent { event ->
            ImGui_ImplSDL3_ProcessEvent(event.ptr)
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
//        renderer.setDrawColor(red, green, blue, SDL_ALPHA_OPAQUE_FLOAT)
//        renderer.clear()

        val dstRect = fRect(
            (SDL_sin(now.toDouble()).toFloat() * 30f) + 100,
            (SDL_cos(now.toDouble()).toFloat() * 30f) + 100,
//            texture.width,
//            texture.height
        )

//        renderer.drawTexture(texture, null, dstRect)
//        renderer.present()

        ImGui_ImplSDLGPU3_NewFrame()
        ImGui_ImplSDL3_NewFrame()
        igNewFrame()

        if (show_demo_window.value)
            igShowDemoWindow(show_demo_window.ptr);

        igRender()
        val drawData = igGetDrawData()
        val commandBuffer = SDL_AcquireGPUCommandBuffer(gpuDevice)
        if (commandBuffer == null) {
            SDL_Log("Error: SDL_AcquireGPUCommandBuffer(): %s", SDL_GetError())
        }


        val swapchain_texture = nativeHeap.allocPointerTo<SDL_GPUTexture>()

        if (!SDL_WaitAndAcquireGPUSwapchainTexture(commandBuffer, window.raw, swapchain_texture.ptr, null, null)) {
            SDL_Log("SDL_AcquireGPUSwapchainTexture failed %s", SDL_GetError())
        }
        if (swapchain_texture.value != null) {
            Imgui_ImplSDLGPU3_PrepareDrawData(drawData, commandBuffer);

            val target_info = nativeHeap.alloc<SDL_GPUColorTargetInfo>()
            target_info.texture = swapchain_texture.value
            target_info.clear_color.r = red.toFloat();
            target_info.clear_color.g = green.toFloat();
            target_info.clear_color.b = blue.toFloat()
            target_info.clear_color.a = 1.0f;
            target_info.load_op = SDL_GPULoadOp.SDL_GPU_LOADOP_CLEAR;
            target_info.store_op = SDL_GPUStoreOp.SDL_GPU_STOREOP_STORE;
            target_info.mip_level = 0u;
            target_info.layer_or_depth_plane = 0u;
            target_info.cycle = false;
            target_info.resolve_texture = null;
            target_info.resolve_mip_level = 0u;
            target_info.resolve_layer = 0u;
            target_info.cycle_resolve_texture = false;
            target_info.padding1 = 0u;
            target_info.padding2 = 0u;
            val renderPass = SDL_BeginGPURenderPass(commandBuffer, target_info.ptr, 1u, null);
            if (renderPass == null) {
                SDL_Log("renderPass is null %s", SDL_GetError() )
            }
            nativeHeap.free(target_info)
            ImGui_ImplSDLGPU3_RenderDrawData(drawData, commandBuffer, renderPass, null);
            SDL_EndGPURenderPass(renderPass);
        }

        SDL_SubmitGPUCommandBuffer(commandBuffer);
        nativeHeap.free(swapchain_texture)

    }
    SDL_WaitForGPUIdle(gpuDevice);
    ImGui_ImplSDL3_Shutdown();
    ImGui_ImplSDLGPU3_Shutdown();
    igDestroyContext(null);
    SDL_ReleaseWindowFromGPUDevice(gpuDevice, window.raw);
    SDL_DestroyGPUDevice(gpuDevice);
//    texture.destroy()
//    renderer.destroy()
    window.destroy()
    Sdl.quit()
}