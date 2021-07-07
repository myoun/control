package kr.myoung2.control

import io.github.monun.kommand.kommand
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ControlPlugin : JavaPlugin() {

    private var controlFile:File = File(dataFolder,"control.yml")
    private var controlConfig:YamlConfiguration = YamlConfiguration()


    // Setup
    override fun onEnable() {
        load(controlConfig,controlFile)
        setupPermissions()
        setupCommands()
        setupControls()
        server.pluginManager.registerEvents(EventListener(this),this)
    }

    override fun onDisable() {
        save(controlConfig,controlFile)
    }

    fun load(config:YamlConfiguration, file: File) {
        if (!file.exists()) {
            file.createNewFile()
        }
        config.load(file)
    }
    fun save(config: YamlConfiguration,file: File) = config.save(file)
    private fun setupCommands() = kommand { ControlCommand.register(this@ControlPlugin,this)}
    private fun setupControls() {
        for (player in server.onlinePlayers) {
            for (value in Control.controls) {
                if (controlConfig.contains(player.controlName(value))) {
                    continue
                }
                controlConfig.set(player.controlName(value),value.default)
            }
        }
    }
    fun setupPermissions() {
        server.pluginManager.addPermission(Permission("control.get"))
        server.pluginManager.addPermission(Permission("control.set"))
        server.pluginManager.addPermission(Permission("control.reset"))

    }

    // Control

    // Set Control

    fun unsafeControl(player: Player,control: Control<*>,value:Any?) {
        controlConfig.set(player.controlName(control),value)
    }
    private fun setDefaultControl(player:Player, control: Control<*>) {
        unsafeControl(player,control,control.default)
    }

    // Get Control

    fun getControl(player: Player,control: Control<*>) : Any? {
        return controlConfig.get(player.controlName(control))
    }
    fun checkFirstTime(player: Player) : Boolean {
        return if (controlConfig.contains(player.uniqueId.toString())) {
            true
        } else {
            for (value in Control.controls) {
                unsafeControl(player,value,value.default)
            }
            false
        }
    }

    // ResetControl

    fun resetEveryControl() {
        controlConfig = YamlConfiguration()
        save(controlConfig,controlFile)
        load(controlConfig,controlFile)
        setupControls()
    }
    fun resetPlayerControl(player: Player) {
        for (control in Control.controls) {
            setDefaultControl(player,control)
        }
    }
    fun resetPlayerSpecificControl(player: Player,control: Control<*>) {
        setDefaultControl(player,control)
    }
    fun resetEverySpecificControl(control:Control<*>) {
        for (player in server.onlinePlayers) {
            resetPlayerSpecificControl(player,control)
        }
    }
}