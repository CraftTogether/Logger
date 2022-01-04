package com.github.crafttogether.logger

import com.destroystokyo.paper.event.block.TNTPrimeEvent
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

class Listener : Listener {

    private val ignitedTnt = mutableMapOf<Vector3, Player>()

    private fun log(url: String?, name: String, vararg detail: String) {
        url ?: return
        val embed = Webhook.EmbedObject()
            .setTitle(name)
        detail.forEachIndexed { i, it ->
            if (i == 0) embed.description = it
            else embed.description += "\n$it"
        }

        Webhook(url)
            .addEmbed(embed)
            .send()
    }

    @EventHandler
    fun onTntIgnite(event: TNTPrimeEvent) {
        val vector3 = Vector3(event.block.x, event.block.y, event.block.z)
        val player = ignitedTnt.remove(vector3)
        log(Logger.config.getString("tnt"), "TNT got ignited",
            "**:bust_in_silhouette:Ignited by:** ${player?.name ?: "unknown"}",
            "**:firecracker:Cause:** ${event.reason.name}",
            "**:compass:Coordinates: ** ${vector3.coords}")
    }

    @EventHandler
    fun onItemInteraction(event: PlayerInteractEvent) {
        if (event.material == Material.FLINT_AND_STEEL && event.clickedBlock?.blockData?.material == Material.TNT) {
            val block = event.clickedBlock!!
            val vector3 = Vector3(block.x, block.y, block.z)
            ignitedTnt[vector3] = event.player
        }
    }

    @EventHandler
    fun onEntityHit(event: EntityDamageByEntityEvent) {
        val entitiesToLog = mutableListOf(
            EntityType.VILLAGER,
            EntityType.PLAYER,

            EntityType.MINECART,
            EntityType.MINECART_CHEST,
            EntityType.MINECART_FURNACE,
            EntityType.MINECART_HOPPER,
            EntityType.MINECART_TNT,
        )
        if (!entitiesToLog.contains(event.entityType)) return

        val entity = event.entity
        if (entity is LivingEntity) {
            if (entity.health - event.damage <= 0.0) {
                val vector3 = Vector3(entity.location.blockX, entity.location.blockY, entity.location.blockZ)
                log(Logger.config.getString("kill"), "Entity got killed",
                    "**:dart:Target:** ${entity.name}",
                    "**:detective:Offender: ** ${event.damager.name}",
                    "**:dagger:Reason:** ${entity.lastDamageCause?.cause?.name ?: "unknown"}",
                    "**:compass:Coordinates: ** ${vector3.coords}")
            }
        }
    }

}