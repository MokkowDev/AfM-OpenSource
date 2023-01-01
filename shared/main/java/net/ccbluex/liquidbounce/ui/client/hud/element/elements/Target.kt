/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.FloatValue
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

/**
 * A target hud
 */
@ElementInfo(name = "Target")
class Target : Element() {

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    private val fadeSpeed = FloatValue("FadeSpeed", 2F, 1F, 9F)
    private val rdeef = DecimalFormat("00.0")
    private val rdeef2 = DecimalFormat("0.0")

    private var easingHealth: Float = 0F
    private var lastTarget: IEntity? = null
    private var armor: Float = 0F
    private var font = Fonts.rise

    override fun drawElement(): Border {
        val target = (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).target

        if (classProvider.isEntityPlayer(target) && target != null) {
            if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth ||
                abs(easingHealth - target.health) < 0.01) {
                easingHealth = target.health
            }

            val width = (38 + (target.name?.let(font::getStringWidth) ?: 0))
                .coerceAtLeast(118)
                .toFloat()

            // Draw rect box
            RenderUtils.drawBorderedRect(0F, 0F, width, 50F, 10F, Color(0,0,0,100).rgb, Color(0,0,0,100).rgb)

            // Damage animation
            if (easingHealth > target.health)
                RenderUtils.drawRect(0F, 34F, (easingHealth / target.maxHealth) * width,
                    36F, Color(43,255,0).rgb)

            // Health bar
            RenderUtils.drawRect(0F, 34F, (target.health / target.maxHealth) * width,
                36F, Color(43,255,0).rgb)

            // Win lose system
            if ((easingHealth - target.health) > 0)
                font.drawStringWithShadow("Winning !", 42, 40, Color(135, 255, 45).rgb)
            if ((easingHealth - target.health) < 0)
                font.drawStringWithShadow("Losing !", 42, 40, Color(200, 20, 20).rgb)
            if ((easingHealth - target.health) == 0.toFloat()) {
                font.drawStringWithShadow("Drawing !", 42, 40, Color(255, 213, 20).rgb)
            }

            // Heal animation
            if (easingHealth < target.health)
                RenderUtils.drawRect((easingHealth / target.maxHealth) * width, 34F,
                    (target.health / target.maxHealth) * width, 36F, Color(255,60,60).rgb)

            easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

            target.name?.let { font.drawString(it, 36, 3, 0xffffff) }
            font.drawString("Distance: ${decimalFormat.format(mc.thePlayer!!.getDistanceToEntityBox(target))}", 36, 15, 0xffffff)

            // Draw info
            val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
            if (playerInfo != null) {
                font.drawString("Health: ${if (target.health < 10) rdeef2.format(target.health) else rdeef.format(target.health)}",
                    36, 24, 0xffffff)
                // Draw head
                val locationSkin = playerInfo.locationSkin
                drawHead(locationSkin, 30, 30)


            }
        }

        lastTarget = target
        return Border(0F, 0F, 120F, 36F)
    }

    private fun drawHead(skin: IResourceLocation, width: Int, height: Int) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        RenderUtils.drawScaledCustomSizeModalRect(2, 2, 8F, 8F, 8, 8, width, height,
            64F, 64F)
    }

}