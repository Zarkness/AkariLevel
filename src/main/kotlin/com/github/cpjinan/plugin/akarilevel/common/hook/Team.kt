package com.github.cpjinan.plugin.akarilevel.common.hook

import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import com.github.cpjinan.plugin.akarilevel.common.event.exp.PlayerExpChangeEvent
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager.getShareLeaderWeight
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager.getShareMemberWeight
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager.getShareTotal
import org.bukkit.Bukkit
import org.serverct.ersha.dungeon.DungeonPlus
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.compileJS
import kotlin.math.roundToLong

object Team {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        val eventPlayer = event.player
        if (ConfigManager.isEnabledTeam() && Bukkit.getServer().pluginManager.isPluginEnabled(ConfigManager.getTeamPlugin()) && event.source in ConfigManager.getShareSource()) {
            when (ConfigManager.getTeamPlugin()) {
                "DungeonPlus" -> {
                    val team = DungeonPlus.teamManager.getTeam(eventPlayer) ?: return
                    val totalAmount =
                        getShareTotal()
                            .replace("%exp%", event.expAmount.toString(), true)
                            .compileJS()?.eval()?.toString()?.toDouble()?.roundToLong() ?: event.expAmount
                    val totalWeight =
                        getShareLeaderWeight() + (team.players.size - 1) * getShareMemberWeight()
                    val leaderAmount =
                        (totalAmount * (getShareLeaderWeight().toDouble() / totalWeight)).roundToLong()
                    val memberAmount =
                        (totalAmount * (getShareMemberWeight().toDouble() / totalWeight)).roundToLong()
                    team.players.forEach {
                        if (it == event.player.uniqueId) {
                            event.expAmount =
                                if (it == team.leader) leaderAmount
                                else memberAmount
                        } else {
                            val player = Bukkit.getPlayer(it) ?: return@forEach
                            PlayerAPI.addPlayerExp(
                                player,
                                event.levelGroup,
                                if (player.uniqueId == team.leader) leaderAmount
                                else memberAmount,
                                "TEAM_SHARE_EXP"
                            )
                        }
                    }
                }

                else -> throw IllegalArgumentException("Unsupported team plugin ${ConfigManager.getTeamPlugin()}.")
            }
        }
    }
}