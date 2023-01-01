/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.CPSCounter
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.extensions.getPing
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.shader.shaders.RainbowFontShader
import net.ccbluex.liquidbounce.value.*
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.sqrt

/**
 * CustomHUD text element
 *
 * Allows to draw custom text
 */
@ElementInfo(name = "Text")
class Text(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F,
           side: Side = Side.default()) : Element(x, y, scale, side) {

    companion object {

        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")

        val DECIMAL_FORMAT = DecimalFormat("0.00")
        val HEALTH_FORMAT = DecimalFormat("0")

        /**
         * Create default element
         */
        fun defaultClient(): Text {
            val text = Text(x = 2.0, y = 2.0, scale = 2F)

            text.line.set(true)
            text.displayString.set("Autodie for mossware")
            text.shadow.set(true)
            text.fontValue.set(Fonts.font35)
            text.setColor(Color(0, 255, 0))

            return text
        }

    }

    private val displayString = TextValue("DisplayText", "")
    private val redrectValue = IntegerValue("Background-Red", 0, 0, 255)
    private val greenrectValue = IntegerValue("Background-Green", 0, 0, 255)
    private val bluerectValue = IntegerValue("Background-Blue", 0, 0, 255)
    private val alpharectValue = IntegerValue("Background-Alpha", 100, 0, 200)
    private val saturationValue = FloatValue("Saturation", 0.9f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val redValue = IntegerValue("Text-Red", 255, 0, 255)
    private val greenValue = IntegerValue("Text-Green", 255, 0, 255)
    private val blueValue = IntegerValue("Text-Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Text-Alpha", 255, 0, 255)
    private val rainbow = BoolValue("Rainbow", false)
    private val rainbowX = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)
    private val background = BoolValue("Background", true)
    private val shadow = BoolValue("Font-Shadow", true)
    private val line = BoolValue("Line", true)
    private var fontValue = FontValue("Font", Fonts.font40)

    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L

    private var displayText = display

    private val display: String
        get() {
            val textContent = if (displayString.get().isEmpty() && !editMode)
                "Add a text in there bruh :/"
            else
                displayString.get()


            return multiReplace(textContent)
        }

    private fun getReplacement(str: String): String? {
        val thePlayer = mc.thePlayer

        if (thePlayer != null) {
            when (str.toLowerCase()) {
                "x" -> return DECIMAL_FORMAT.format(thePlayer.posX)
                "y" -> return DECIMAL_FORMAT.format(thePlayer.posY)
                "z" -> return DECIMAL_FORMAT.format(thePlayer.posZ)
                "xdp" -> return thePlayer.posX.toString()
                "ydp" -> return thePlayer.posY.toString()
                "zdp" -> return thePlayer.posZ.toString()
                "velocity" -> return DECIMAL_FORMAT.format(sqrt(thePlayer.motionX * thePlayer.motionX + thePlayer.motionZ * thePlayer.motionZ))
                "ping" -> return thePlayer.getPing().toString()
                "health" -> return HEALTH_FORMAT.format(thePlayer.health)
                "maxhealth" -> return HEALTH_FORMAT.format(thePlayer.maxHealth)
                "hp" -> return HEALTH_FORMAT.format(thePlayer.health)
                "maxhp" -> return HEALTH_FORMAT.format(thePlayer.maxHealth)
                "food" -> return thePlayer.foodStats.foodLevel.toString()
                "onground" -> return thePlayer.onGround.toString()
                "dead" -> return thePlayer.isDead.toString()
                "moss" -> return "Moss is the noice guy!"
            }
        }

        return when (str.toLowerCase()) {
            "username" -> mc.session.username
            "clientname" -> LiquidBounce.CLIENT_NAME
            "clientversion" -> "${LiquidBounce.CLIENT_VERSION}"
            "clientcreator" -> LiquidBounce.CLIENT_CREATOR
            "fps" -> mc.debugFPS.toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverip" -> ServerUtils.getRemoteIp()
            "cps", "lcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()
            "build" -> return "${LiquidBounce.CLIENT_BUILD} §7- §a${mc.session.username}"
            else -> null // Null = don't replace
        }
    }

    private fun multiReplace(str: String): String {
        var lastPercent = -1
        val result = StringBuilder()
        for (i in str.indices) {
            if (str[i] == '%') {
                if (lastPercent != -1) {
                    if (lastPercent + 1 != i) {
                        val replacement = getReplacement(str.substring(lastPercent + 1, i))

                        if (replacement != null) {
                            result.append(replacement)
                            lastPercent = -1
                            continue
                        }
                    }
                    result.append(str, lastPercent, i)
                }
                lastPercent = i
            } else if (lastPercent == -1) {
                result.append(str[i])
            }
        }

        if (lastPercent != -1) {
            result.append(str, lastPercent, str.length)
        }

        return result.toString()
    }

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val color = Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get()).rgb

        val fontRenderer = fontValue.get()

        val rainbow = rainbow.get()

        if(line.get()) {
            // RenderUtils.drawRect(-2F, -3F, fontRenderer.getStringWidth(displayText) + 2F, -2F, RenderUtils.testRainbow(0, saturationValue.get(), brightnessValue.get(), 5.0))
            RenderUtils.drawRect(-2F, -3F, fontRenderer.getStringWidth(displayText) + 2F, -2F, ColorUtils.rainbow(400000000L).rgb)
        }

        if (background.get()) {
            val rectColor = Color(redrectValue.get(), greenrectValue.get(), bluerectValue.get(), alpharectValue.get()).rgb
            val expand = fontRenderer.fontHeight * 0.3F

            RenderUtils.drawRect(-expand,-expand,fontRenderer.getStringWidth(displayText)+expand,fontRenderer.fontHeight+expand,rectColor)
        }

        RainbowFontShader.begin(
            rainbow,
            if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
            if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
            System.currentTimeMillis() % 10000 / 10000F
        ).use {
            fontRenderer.drawString(
                displayText, 0F, 0F, if (rainbow)
                    0 else color, shadow.get()
            )

            if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen) && editTicks <= 40)
                fontRenderer.drawString(
                    "_", fontRenderer.getStringWidth(displayText) + 2F,
                    0F, if (rainbow) ColorUtils.rainbow(400000000L).rgb else color, shadow.get()
                )
        }

        if (editMode && !classProvider.isGuiHudDesigner(mc.currentScreen)) {
            editMode = false
            updateElement()
        }

        return Border(
            -2F,
            -2F,
            fontRenderer.getStringWidth(displayText) + 2F,
            fontRenderer.fontHeight.toFloat()
        )
    }

    override fun updateElement() {
        editTicks += 5
        if (editTicks > 80) editTicks = 0

        displayText = if (editMode) displayString.get() else display
    }

    override fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {
        if (isInBorder(x, y) && mouseButton == 0) {
            if (System.currentTimeMillis() - prevClick <= 250L)
                editMode = true

            prevClick = System.currentTimeMillis()
        } else {
            editMode = false
        }
    }

    override fun handleKey(c: Char, keyCode: Int) {
        if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (displayString.get().isNotEmpty())
                    displayString.set(displayString.get().substring(0, displayString.get().length - 1))

                updateElement()
                return
            }

            if (ColorUtils.isAllowedCharacter(c) || c == '§')
                displayString.set(displayString.get() + c)

            updateElement()
        }
    }

    fun setColor(c: Color): Text {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }
}
