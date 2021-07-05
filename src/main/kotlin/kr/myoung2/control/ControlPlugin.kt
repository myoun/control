package kr.myoung2.control

import io.github.monun.kommand.kommand
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ControlPlugin : JavaPlugin() {

    private var controlFile:File = File(dataFolder,"control.yml")
    private var controlConfig:YamlConfiguration = YamlConfiguration()

    override fun onEnable() {
        load(controlConfig,controlFile)
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

    fun unsafeControl(player: Player,control: Control<*>,value:Any?) {
        controlConfig.set(player.controlName(control),value)
    }

    fun getControl(player: Player,control: Control<*>) : Any? {
        return controlConfig.get(player.controlName(control))
    }

    fun resetEveryControl() {
        controlConfig = YamlConfiguration()
        save(controlConfig,controlFile)
        load(controlConfig,controlFile)
        setupControls()
    }

    @Suppress("UNUSED")
    fun resetPlayerControl(player: Player) {
        controlConfig.set(player.uniqueId.toString(),null)
        save(controlConfig,controlFile)
        load(controlConfig,controlFile)
        setupControls()
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
}