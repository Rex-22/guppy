package net.ruben.sdl

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString

private data class CallbackData(
    val callback: (userData: COpaquePointer?, category: Int, priority: SDL_LogPriority, message: String?) -> Unit,
    val userdata: COpaquePointer?
) {}

object SdlLog {
    fun setLogCallback(
        userData: COpaquePointer? = null,
        callback: (userData: COpaquePointer?, category: Int, priority: SDL_LogPriority, message: String?) -> Unit,
    ) {
        var data = CallbackData(callback, userData)
        val stableRef = StableRef.create(data)
        val voidPtr = stableRef.asCPointer()

        SDL_SetLogOutputFunction(staticCFunction { userData, category, priority, message ->
            val stableRef = userData?.asStableRef<CallbackData>()
            val c = stableRef?.get()?.callback
            val userData = stableRef?.get()?.userdata
            c?.invoke(userData, category, priority, message?.toKString())
        }, voidPtr)
    }
}