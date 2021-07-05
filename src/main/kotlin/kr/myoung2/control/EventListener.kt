package kr.myoung2.control

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*

class EventListener(val plugin:ControlPlugin) : Listener {

    private fun checkControlBoolean(control:Control<Boolean>,player:Player) : Boolean = plugin.getControl(player,control) as Boolean

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.checkFirstTime(event.player)
    }


    @EventHandler
    fun onChat(event:AsyncChatEvent) {
        if (!checkControlBoolean(Control.CHAT,event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!checkControlBoolean(Control.MOVE, event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBreak(event:BlockBreakEvent) {
        if (!checkControlBoolean(Control.BREAK,event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        if (!checkControlBoolean(Control.PLACE,event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDrop(event:PlayerDropItemEvent) {
        if (!checkControlBoolean(Control.DROP,event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onAttack(event:EntityDamageByEntityEvent) {
        var damager: Any = event.damager

        if (damager is Projectile) {
            damager = damager.shooter ?: return
        }

        if (damager is Player)
            if (!checkControlBoolean(Control.ATTACK,damager))
                event.isCancelled = true
    }

    @EventHandler
    fun onDamage(event:EntityDamageEvent) {
        if (event.entity is Player)
            if (!checkControlBoolean(Control.DAMAGE, event.entity as Player))
                event.isCancelled = true
    }

    @EventHandler
    fun onInteract(event:PlayerInteractEvent) {
        if (!checkControlBoolean(Control.INTERACT,event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onFish(event:PlayerFishEvent) {
        if (!checkControlBoolean(Control.FISH,event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDeath(event:PlayerDeathEvent) {
        if (!checkControlBoolean(Control.DEATH,event.entity)) {
            event.isCancelled = true
        }

    }

    @EventHandler
    fun onPick(event:EntityPickupItemEvent) {
        if (event.entity is Player) {
            if (!checkControlBoolean(Control.PICK,event.entity as Player))
                event.isCancelled = true
        }
    }


}