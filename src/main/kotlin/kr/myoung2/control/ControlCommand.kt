package kr.myoung2.control

import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandDispatcherBuilder
import io.github.monun.kommand.argument.KommandArgument
import io.github.monun.kommand.argument.PlayerArgument
import io.github.monun.kommand.argument.suggest
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ControlCommand {

    private lateinit var plugin:ControlPlugin

    internal fun register(plugin:ControlPlugin, builder:KommandDispatcherBuilder) {
        this.plugin = plugin
        builder.register("control") {
            require {
                if (it.isOp)
                    true
                else it.hasPermission("control")
            }
            then("player" to PlayerArgument) {
                then("control") {
                    then("control" to ControlArgument) {
                        then("value" to ValueArgument) {
                            executes {
                                val player = it.parseArgument<Player>("player")
                                val control = it.parseArgument<Control<*>>("control")
                                val value = it.parseArgument<Any>("value")
                                val sender = it.sender
                                plugin.unsafeControl(player,control,value)
                                sender.sendColorMessage("Player : ${player.name}\nControl : ${control.name}\nValue : $value",ChatColor.GOLD)
                            }
                        }
                        executes {
                            val sender = it.sender
                            val player = it.parseArgument<Player>("player")
                            val control = it.parseArgument<Control<*>>("control")
                            sender.sendColorMessage("${player.name}'s ${control.name} value : ${plugin.getControl(player,control)}",ChatColor.GOLD)
                        }

                    }
                }
                then("reset") {
                    executes {
                        plugin.resetEveryControl()
                    }
                }
            }
/*
        [Not Active]
            then("everyone" to EveryoneArgument) {
                then("control") {
                    then("control" to ControlArgument) {
                        then("value" to ValueArgument) {
                            executes {
                                val control = it.parseArgument<Control<*>>("control")
                                val value = it.parseArgument<Any>("value")
                                val sender = it.sender
                                for (player in plugin.server.onlinePlayers) {
                                    plugin.unsafeControl(player, control, value)
                                }
                            }
                        }
                    }
                }
                then ("reset") {
                    executes {
                        plugin.resetEveryControl()
                    }
                }
            }

 */
        }
    }

    object ControlArgument : KommandArgument<Control<*>> {

        override fun parse(context: KommandContext, param: String): Control<*>? {
            return Control.controls.find { it.name.equals(param,true) }
        }

        override fun suggest(context: KommandContext, target: String): Collection<String> {
            return Control.controls.suggest(target) { it.name }

        }
    }

    object ValueArgument : KommandArgument<Boolean> {

        override fun parse(context: KommandContext, param: String): Boolean? {
            return when(param) {
                "true" -> true
                "false" -> false
                else -> null
            }
        }

        override fun suggest(context: KommandContext, target: String): Collection<String> {
            val control = context.parseOrNullArgument<Control<*>>("control") ?: return emptyList()
            val type = control.type
            return if (type == (Boolean::class)) listOf("true","false") else emptyList()
        }
    }

    object EveryoneArgument : KommandArgument<Collection<Player>> {
        override fun parse(context: KommandContext, param: String): Collection<Player> {
            return plugin.server.onlinePlayers
        }

        override fun suggest(context: KommandContext, target: String): Collection<String> {
            return listOf("everyone")
        }
    }

}

fun CommandSender.sendColorMessage(message:String,color:ChatColor) {
    this.sendMessage("${color}${message}")
}