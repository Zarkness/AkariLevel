package com.github.cpjinan.plugin.akarilevel.common.listener

import com.github.cpjinan.plugin.akarilevel.api.DataAPI
import com.github.cpjinan.plugin.akarilevel.api.LevelAPI
import com.github.cpjinan.plugin.akarilevel.api.PlayerAPI
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager.getDefaultTrace
import com.github.cpjinan.plugin.akarilevel.internal.manager.ConfigManager.isEnabledAutoResetTrace
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent

object PlayerListener {
    @SubscribeEvent
    fun onPlayerExpChange(event: PlayerExpChangeEvent) {
        LevelAPI.getLevelGroupNames().forEach {
            PlayerAPI.addPlayerExp(event.player, it, event.amount.toLong(), "VANILLA_EXP_CHANGE")
            PlayerAPI.refreshPlayerLevel(event.player, it)
        }
        if (!ConfigManager.isEnabledVanilla()) event.amount = 0
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PlayerAPI.refreshPlayerLevel(event.player)
        if (isEnabledAutoResetTrace()) PlayerAPI.setPlayerTraceLevelGroup(event.player, getDefaultTrace())
    }

    @SubscribeEvent
    fun onPlayerQuit(event: PlayerQuitEvent) {
        DataAPI.saveData()
    }
}