/*
 * MIT License
 *
 * Copyright (c) 2020 Melms Media LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.octave.bot.music.utils

import gg.octave.bot.Launcher
import gg.octave.bot.db.OptionsRegistry
import gg.octave.bot.utils.Scheduler
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PlayerSweeper {
    private val registry = Launcher.players
    private val executor = Executors.newSingleThreadScheduledExecutor()

    fun runEvery(timeUnit: TimeUnit, amount: Long) {
        Scheduler.fixedRateScheduleWithSuppression(executor, amount, amount, timeUnit) {
            Launcher.players.registry.values
                .filter {
                    // If guild null, or if connected, and not playing, and not queued for leave,
                    // if last played >= IDLE_TIMEOUT minutes ago, and not 24/7 (all day) music, destroy/queue leave.
                    it.guild == null || it.guild!!.audioManager.isConnected && it.player.playingTrack == null &&
                        !it.leaveQueued && System.currentTimeMillis() - it.lastPlayedAt > 120000 &&
                        !isAllDayMusic(it.guildId)
                }
                .forEach {
                    if (it.guild == null) {
                        registry.destroy(it.guildId.toLong())
                    } else {
                        it.queueLeave() //Then queue leave.
                    }
                }
        }
    }

    private fun isAllDayMusic(guildId: String) : Boolean {
        val premium = Launcher.database.getPremiumGuild(guildId)
        val guildData = OptionsRegistry.ofGuild(guildId)
        val key = guildData.isPremium

        return (premium != null || key) && guildData.music.isAllDayMusic
    }
}