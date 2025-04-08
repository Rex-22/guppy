#ifdef CIMGUI_USE_SDL3
#ifdef CIMGUI_DEFINE_ENUMS_AND_STRUCTS

typedef enum { ImGui_ImplSDL3_GamepadMode_AutoFirst, ImGui_ImplSDL3_GamepadMode_AutoAll, ImGui_ImplSDL3_GamepadMode_Manual }ImGui_ImplSDL3_GamepadMode;
#endif //CIMGUI_DEFINE_ENUMS_AND_STRUCTS
CIMGUI_API bool ImGui_ImplSDL3_InitForOpenGL(SDL_Window* window,void* sdl_gl_context);
CIMGUI_API bool ImGui_ImplSDL3_InitForVulkan(SDL_Window* window);
CIMGUI_API bool ImGui_ImplSDL3_InitForD3D(SDL_Window* window);
CIMGUI_API bool ImGui_ImplSDL3_InitForMetal(SDL_Window* window);
CIMGUI_API bool ImGui_ImplSDL3_InitForSDLRenderer(SDL_Window* window,SDL_Renderer* renderer);
CIMGUI_API bool ImGui_ImplSDL3_InitForSDLGPU(SDL_Window* window);
CIMGUI_API bool ImGui_ImplSDL3_InitForOther(SDL_Window* window);
CIMGUI_API void ImGui_ImplSDL3_Shutdown(void);
CIMGUI_API void ImGui_ImplSDL3_NewFrame(void);
CIMGUI_API bool ImGui_ImplSDL3_ProcessEvent(const SDL_Event* event);
CIMGUI_API void ImGui_ImplSDL3_SetGamepadMode(ImGui_ImplSDL3_GamepadMode mode,SDL_Gamepad** manual_gamepads_array,int manual_gamepads_count);

#endif
#ifdef CIMGUI_USE_SDLGPU3
#ifdef CIMGUI_DEFINE_ENUMS_AND_STRUCTS

typedef struct ImGui_ImplSDLGPU3_InitInfo ImGui_ImplSDLGPU3_InitInfo;
struct ImGui_ImplSDLGPU3_InitInfo
{
    SDL_GPUDevice*       Device;
    SDL_GPUTextureFormat ColorTargetFormat;
    SDL_GPUSampleCount   MSAASamples;
};
#endif //CIMGUI_DEFINE_ENUMS_AND_STRUCTS
CIMGUI_API _Bool ImGui_ImplSDLGPU3_Init(ImGui_ImplSDLGPU3_InitInfo* info);
CIMGUI_API void ImGui_ImplSDLGPU3_Shutdown(void);
CIMGUI_API void ImGui_ImplSDLGPU3_NewFrame(void);
CIMGUI_API void Imgui_ImplSDLGPU3_PrepareDrawData(ImDrawData* draw_data,SDL_GPUCommandBuffer* command_buffer);
CIMGUI_API void ImGui_ImplSDLGPU3_RenderDrawData(ImDrawData* draw_data,SDL_GPUCommandBuffer* command_buffer,SDL_GPURenderPass* render_pass,SDL_GPUGraphicsPipeline* pipeline);
CIMGUI_API void ImGui_ImplSDLGPU3_CreateDeviceObjects(void);
CIMGUI_API void ImGui_ImplSDLGPU3_DestroyDeviceObjects(void);
CIMGUI_API void ImGui_ImplSDLGPU3_CreateFontsTexture(void);
CIMGUI_API void ImGui_ImplSDLGPU3_DestroyFontsTexture(void);

#endif
