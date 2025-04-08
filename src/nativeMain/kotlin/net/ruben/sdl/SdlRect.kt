package net.ruben.sdl

import kotlinx.cinterop.NativePlacement
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap

fun fRect(
    x: Float = 0F,
    y: Float = 0F,
    w: Float = 0F,
    h: Float = 0F,
    allocator: NativePlacement = nativeHeap,
): SDL_FRect = allocator.fRect(x, y, w, h)

fun NativePlacement.fRect(
    x: Float = 0F,
    y: Float = 0F,
    w: Float = 0F,
    h: Float = 0F,
): SDL_FRect = alloc<SDL_FRect> {
    this.x = x
    this.y = y
    this.w = w
    this.h = h
}

operator fun SDL_FRect.component1(): Float = this.x
operator fun SDL_FRect.component2(): Float = this.y
operator fun SDL_FRect.component3(): Float = this.w
operator fun SDL_FRect.component4(): Float = this.h

/**
 * Create a new [SDL_FRect]
 *
 * Important to note that this uses [nativeHeap] as an allocator when [SDL_FRect] is created.
 * However, if needed you can pass your own allocator via [allocator]
 */
fun rect(
    x: Int = 0,
    y: Int = 0,
    w: Int = 0,
    h: Int = 0,
    allocator: NativePlacement = nativeHeap,
): SDL_Rect = allocator.rect(x, y, w, h)

fun NativePlacement.rect(
    x: Int = 0,
    y: Int = 0,
    w: Int = 0,
    h: Int = 0,
): SDL_Rect = alloc<SDL_Rect> {
    this.x = x
    this.y = y
    this.w = w
    this.h = h
}

operator fun SDL_Rect.component1(): Int = this.x
operator fun SDL_Rect.component2(): Int = this.y
operator fun SDL_Rect.component3(): Int = this.w
operator fun SDL_Rect.component4(): Int = this.h
