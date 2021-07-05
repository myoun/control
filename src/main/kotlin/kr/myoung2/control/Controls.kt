package kr.myoung2.control

import org.bukkit.entity.Player
import kotlin.reflect.KClass

sealed class Control<T>(val name:String, val type: KClass<*>, val default:T) {

    object CHAT : Control<Boolean>("chat",Boolean::class,true)
    object MOVE : Control<Boolean>("move",Boolean::class, true)
    object BREAK : Control<Boolean>("break",Boolean::class, true)
    object PLACE : Control<Boolean>("place",Boolean::class, true)
    object DROP : Control<Boolean>("drop",Boolean::class, true)
    object ATTACK : Control<Boolean>("attack",Boolean::class, true)
    object DAMAGE : Control<Boolean>("damage",Boolean::class, true)
    object INTERACT : Control<Boolean>("interact",Boolean::class,true)
    object FISH : Control<Boolean>("fish",Boolean::class,true)
    object DEATH : Control<Boolean>("death",Boolean::class,true)
    object PICK : Control<Boolean>("pick",Boolean::class,true)




    companion object {
        val controls:ArrayList<Control<*>> = arrayListOf(
            CHAT,
            MOVE,
            BREAK,
            PLACE,
            ATTACK,
            DAMAGE,
            DROP,
            INTERACT,
            FISH,
            DEATH,
            PICK
        )
    }

    override fun toString(): String {
        return name
    }
}

fun Player.controlName(control: Control<*>) = "${this.uniqueId}.${control.name}"