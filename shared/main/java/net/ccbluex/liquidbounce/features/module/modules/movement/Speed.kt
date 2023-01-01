package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.ncp.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.spartan.*
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import java.util.*

@ModuleInfo(name = "Speed", description = "Kill server go brrrrr", category = ModuleCategory.MOVEMENT)
class Speed : Module() {
    private val speedModes = arrayOf( // Vanilla
        VanillaBhop(), // NCP
        VanillaGround(),
        NCP(),
        NCPYPort(),
        OnGround(), // AAC
        AACHop3313(),
        AACHop350(),
        AACLowHop(),
        AACLowHop2(),
        AACLowHop3(),
        AACGround(),
        AACYPort(),
        OldAACBHop(), // Spartan
        TestSpartan(), // Server and anticheat
        VulcanGround(),
        BlockDropAbuse(),
        BlockDrop(),
        Hypixel(),
        HypixelSafe(),
        VerusSlow(),
        VerusFastDEV(),
        CubeCraft(),
        Mineplex(),
        RedeskyAAC5(), // Other
        SlowHop(),
        CustomSpeed()
    )

    val modeValue: ListValue = object : ListValue("Mode", modes, "Hypixel") {
        override fun onChange(oldValue: String, newValue: String) {
            if (state)
                onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state)
                onEnable()
        }
    }

    val vanillabhopSpeedValue = FloatValue("VanillaBhop-Speed", 1f, 0.1f, 5f)
    val customSpeedValue = FloatValue("CustomSpeed", 1.6f, 0.2f, 2f)
    val customYValue = FloatValue("CustomY", 0f, 0f, 4f)
    val customTimerValue = FloatValue("CustomTimer", 1f, 0.1f, 2f)
    val portMax = FloatValue("AAC-PortLength", 1f, 1f, 20f)
    val aacGroundTimerValue = FloatValue("AACGround-Timer", 3f, 1.1f, 10f)
    val cubecraftPortLengthValue = FloatValue("CubeCraft-PortLength", 1f, 0.1f, 2f)
    val mineplexGroundSpeedValue = FloatValue("MineplexGround-Speed", 0.5f, 0.1f, 1f)
    private val faststop = BoolValue("FastStop", true)
    val customStrafeValue = BoolValue("CustomStrafe", true)
    val resetXZValue = BoolValue("CustomResetXZ", false)
    val resetYValue = BoolValue("CustomResetY", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.sneaking)
            return

        if (MovementUtils.isMoving) {
            thePlayer.sprinting = true
        }

        mode?.onUpdate()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.sneaking || event.eventState != EventState.PRE)
            return

        if (MovementUtils.isMoving)
            thePlayer.sprinting = true

        mode?.onMotion()
    }

    @EventTarget
    fun onMove(event: MoveEvent?) {
        if (mc.thePlayer!!.sneaking)
            return
        mode?.onMove(event!!)
    }

    @EventTarget
    fun onTick(event: TickEvent?) {
        if (mc.thePlayer!!.sneaking)
            return

        mode?.onTick()
    }

    override fun onEnable() {
        if (mc.thePlayer == null)
            return

        mc.timer.timerSpeed = 1f

        mode?.onEnable()
    }

    override fun onDisable() {
        if(faststop.get()) {
            mc.thePlayer!!.motionX = 0.0
            mc.thePlayer!!.motionY = 0.0
            mc.thePlayer!!.motionZ = 0.0
            mc.timer.timerSpeed = 1.0f
        }
        if (mc.thePlayer == null)
            return

        mc.timer.timerSpeed = 1f

        mode?.onDisable()
    }

    override val tag: String
        get() = modeValue.get()

    private val mode: SpeedMode?
        get() {
            val mode = modeValue.get()

            for (speedMode in speedModes) if (speedMode.modeName.equals(mode, ignoreCase = true))
                return speedMode

            return null
        }

    private val modes: Array<String>
        get() {
            val list: MutableList<String> = ArrayList()
            for (speedMode in speedModes) list.add(speedMode.modeName)
            return list.toTypedArray()
        }
}