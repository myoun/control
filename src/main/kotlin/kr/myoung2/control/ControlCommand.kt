package kr.myoung2.control

import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandDispatcherBuilder
import io.github.monun.kommand.argument.KommandArgument
import io.github.monun.kommand.argument.PlayerArgument
import io.github.monun.kommand.argument.suggest
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission

object ControlCommand {

    private lateinit var plugin:ControlPlugin

    internal fun register(plugin:ControlPlugin,builder: KommandDispatcherBuilder) {
        this.plugin = plugin
        builder.register("control") {
            // Get
            require {it.hasPermission(Permission("control.get"))}
            then("get") {
                then("player" to PlayerArgument) {
                    then("control" to ControlArgument) {
                        executes {
                            val sender = it.sender
                            val player = it.parseArgument<Player>("player")
                            val control = it.parseArgument<Control<*>>("control")

                            sender.sendColorMessage("${player.name}'s ${control.name} value : ${plugin.getControl(player,control)}",ChatColor.GOLD)
                        }
                    }
                }
            }
            // Set
            require { it.hasPermission(Permission("control.set")) }
            then("set") {
                then("player" to PlayerArgument) {
                    then("control" to ControlArgument) {
                        then("value" to ValueArgument) {
                            executes {
                                val sender = it.sender
                                val player = it.parseArgument<Player>("player")
                                val control = it.parseArgument<Control<*>>("control")
                                val value = it.parseArgument<Any>("value")
                                plugin.unsafeControl(player, control, value)
                                sender.sendColorMessage("Changed ${player.name}'s ${control.name} value to $value",ChatColor.GOLD)
                            }
                        }
                    }
                }
            }
            // Reset
            require { it.hasPermission(Permission("control.reset")) }
            then("reset") {
                then ("everyone") {
                    then("control" to ControlArgument) {
                        executes {
                            val sender = it.sender
                            val control = it.parseArgument<Control<*>>("control")
                            plugin.resetEverySpecificControl(control)
                            sender.sendColorMessage("Reset Every ${control.name} control",ChatColor.GOLD)
                        }
                    }
                    executes {
                        plugin.resetEveryControl()
                        it.sender.sendColorMessage("Reset Every control",ChatColor.GOLD)
                    }
                }
                then("player") {
                    then("player" to PlayerArgument) {
                        then("control" to ControlArgument) {
                            executes {
                                val sender = it.sender
                                val player = it.parseArgument<Player>("player")
                                val control = it.parseArgument<Control<*>>("control")
                                plugin.resetPlayerSpecificControl(player,control)
                                sender.sendColorMessage("Reset ${player.name}'s ${control.name}",ChatColor.GOLD)
                            }
                        }
                        executes {
                            val sender = it.sender
                            val player = it.parseArgument<Player>("player")
                            plugin.resetPlayerControl(player)
                            sender.sendColorMessage("Reset ${player.name}",ChatColor.GOLD)
                        }
                    }
                }
            }

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

    object ValueArgument : KommandArgument<Any> {

        override fun parse(context: KommandContext, param: String): Any? {
            return if (context.parseOrNullArgument<Control<*>>("control")?.default is Boolean) {
                when(param) {
                    "true" -> true
                    "false" -> false
                    else -> null
                }
            } else if (context.parseOrNullArgument<Control<*>>("control")?.default is String) {
                param
            } else null
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