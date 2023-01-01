package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.JumpEvent
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "LongJump", description = "Allows you to jump longer then normal.", category = ModuleCategory.MOVEMENT)
class LongJump : Module() {
    private val modeValue =
        ListValue("Mode", arrayOf("NCP", "OldAAC", "OldRedesky", "TeleportAAC"), "Redeshit")
    private val ncpBoostValue = FloatValue("NCPSpeed", 4.25f, 1f, 10f)
    private val autoJumpValue = BoolValue("AutoJump", true)
    private var jumped = false
    private var canBoost = false
    private var teleported = false
    private var canMineplexBoost = false

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (LadderJump.jumped)
            MovementUtils.strafe(MovementUtils.speed * 1.08f)

        val thePlayer = mc.thePlayer ?: return

        if (jumped) {
            val mode = modeValue.get()

            if (thePlayer.onGround || thePlayer.capabilities.isFlying) {
                jumped = false
                canMineplexBoost = false

                if (mode.equals("NCP", ignoreCase = true)) {
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                }
                return
            }
            run {
                when (mode.toLowerCase()) {
                    "ncp" -> {
                        MovementUtils.strafe(MovementUtils.speed * if (canBoost) ncpBoostValue.get() else 1f)
                        canBoost = false
                    }
                    "oldaac" -> {
                        thePlayer.motionY += 0.05999
                        MovementUtils.strafe(MovementUtils.speed * 1.08f)
                    }
                    "oldredesky", "mineplex3" -> {
                        thePlayer.jumpMovementFactor = 0.09f
                        thePlayer.motionY += 0.0132099999999999999999999999999
                        thePlayer.jumpMovementFactor = 0.08f
                        MovementUtils.strafe()
                    }
                    "teleportaac" -> {
                        if (thePlayer.fallDistance > 0.5f && !teleported) {
                            val value = 3.0
                            val horizontalFacing = thePlayer.horizontalFacing
                            var x = 0.0
                            var z = 0.0

                            when {
                                horizontalFacing.isNorth() -> z = -value
                                horizontalFacing.isEast() -> x = +value
                                horizontalFacing.isSouth() -> z = +value
                                horizontalFacing.isWest() -> x = -value
                                else -> {
                                }
                            }

                            thePlayer.setPosition(thePlayer.posX + x, thePlayer.posY, thePlayer.posZ + z)
                            teleported = true
                        }
                    }
                    "mineplex" -> {
                        thePlayer.motionY += 0.0132099999999999999999999999999
                        thePlayer.jumpMovementFactor = 0.08f
                        MovementUtils.strafe()
                    }
                    "mineplex2" -> {
                        if (!canMineplexBoost)
                            return@run

                        thePlayer.jumpMovementFactor = 0.1f
                        if (thePlayer.fallDistance > 1.5f) {
                            thePlayer.jumpMovementFactor = 0f
                            thePlayer.motionY = (-10f).toDouble()
                        }

                        MovementUtils.strafe()
                    }
                    "redeshit" -> {
                        thePlayer.motionY += 0.034f
                    }
                    "nagadev" -> {
                        thePlayer.jumpMovementFactor = 0.19f
                        thePlayer.motionY += 0.053f
                    }
                }
            }
        }
        if (autoJumpValue.get() && thePlayer.onGround && MovementUtils.isMoving) {
            jumped = true
            thePlayer.jump()
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        val thePlayer = mc.thePlayer ?: return
        val mode = modeValue.get()

        if (mode.equals("mineplex3", ignoreCase = true)) {
            if (thePlayer.fallDistance != 0.0f)
                thePlayer.motionY += 0.037
        } else if (mode.equals("ncp", ignoreCase = true) && !MovementUtils.isMoving && jumped) {
            thePlayer.motionX = 0.0
            thePlayer.motionZ = 0.0
            event.zeroXZ()
        }
    }
    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        jumped = true
        canBoost = true
        teleported = false

        if (state) {
            when (modeValue.get().toLowerCase()) {
                "mineplex" -> event.motion = event.motion * 4.08f
                "mineplex2" -> {
                    if (mc.thePlayer!!.isCollidedHorizontally) {
                        event.motion = 2.31f
                        canMineplexBoost = true
                        mc.thePlayer!!.onGround = false
                    }
                }
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
    }
}
