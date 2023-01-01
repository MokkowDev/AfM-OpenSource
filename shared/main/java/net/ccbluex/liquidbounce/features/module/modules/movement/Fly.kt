package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.api.minecraft.util.IAxisAlignedBB
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

@ModuleInfo(name = "Fly", description = "Allows you to fly in survival mode.", category = ModuleCategory.MOVEMENT, keyBind = Keyboard.KEY_F)
class Fly : Module() {
    val modeValue = ListValue("Mode", arrayOf(
        "Vanilla",
        "AntiKick",
        "Creative",

        // AAC
        "AAC1.9.10",
        "AAC3.0.5",

        // Hypixel
        "Hypixel",

        // Rewinside
        "Rewinside",

        // Vulcan
        "Vulcan",

        // Other server specific flys
        "OldRedeshit",
        "NeruxVace",

        // Spartan
        "Spartan",
        "BugSpartan",

        // Other anticheats
        "MatrixLastestBoat",
        "Verus1",
        "Verus2",

        // Other
        "KeepAlive"
    ), "Vanilla")
    private val vanillaSpeedValue = FloatValue("Vanilla-speed", 2f, 0f, 5f)

    // AAC
    private val VerusfastspeedValue = FloatValue("Verus-speed", 2f, 0f, 8f)
    private val aacSpeedValue = FloatValue("AAC1.9.10-Speed", 0.3f, 0f, 1f)
    private val aacFast = BoolValue("AAC3.0.5-Fast", true)

    // Hypixel
    private val hypixelBoost = BoolValue("Hypixel-Boost", true)
    private val hypixelBoostDelay = IntegerValue("Hypixel-BoostDelay", 1200, 0, 2000)
    private val hypixelBoostTimer = FloatValue("Hypixel-BoostTimer", 1f, 0f, 5f)
    private val neruxVaceTicks = IntegerValue("NeruxVace-Ticks", 6, 0, 20)

    // Matrix Boat
    private val matrixSpeed = FloatValue("MatrixLatestBoat-Speed", 0.7f, 0.1f, 3f)
    private val matrixY = IntegerValue("MatrixLatestBoat-Y", 1, 0, 20)
    private val matrixTimer = FloatValue("MatrixLatestBoat-Timer", 0.62f, 0.1f, 6.9f)

    // Visuals
    private val markValue = BoolValue("ESP", true)

    // Var/Val
    private var startY = 0.0
    private var jump = 0
    private var lol = 0
    private val flyTimer = MSTimer()
    private val flyTimer2 = MSTimer()
    private val groundTimer = MSTimer()
    private var noPacketModify = false
    private var aacJump = 0.0
    private var aac3delay = 0
    private var aac3glideDelay = 0
    private var noFlag = false
    private val spartanTimer = TickTimer()
    private var wasDead = false
    private val hypixelTimer = TickTimer()
    private var boostHypixelState = 1
    private var moveSpeed = 0.0
    private var lastDistance = 0.0
    private var failedStart = false
    private val cubecraftTeleportTickTimer = TickTimer()
    private val freeHypixelTimer = TickTimer()
    private var freeHypixelYaw = 0f
    private var freeHypixelPitch = 0f

    override fun onEnable() {

        val x = mc.thePlayer!!.posX
        val y = mc.thePlayer!!.posY
        val z = mc.thePlayer!!.posZ
        startY = mc.thePlayer!!.posY
        val thePlayer = mc.thePlayer ?: return
        ClientUtils.getLogger().info("Lol fly")

        flyTimer.reset()
        noPacketModify = true


        val mode = modeValue.get()

        run {
            when (mode.toLowerCase()) {
                "verus1" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 4, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z,false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z,true))
                    thePlayer.setPosition(thePlayer.posX, thePlayer.posY + 0.5, thePlayer.posZ)
                }
                "vulcan" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 4, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z,false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z,true))
                    thePlayer.setPosition(thePlayer.posX, thePlayer.posY + 1, thePlayer.posZ)
                }
                "verus2" -> {
                    ClientUtils.displayChatMessage("[Autodie for Mossware] Thanks BTHgaming for this fly!")
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 4, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z,false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z,true))
                    thePlayer.setPosition(thePlayer.posX, thePlayer.posY + 0.5, thePlayer.posZ)
                }
                "luckyswboat" -> {
                    mc.thePlayer!!.setPosition(thePlayer.posX, thePlayer.posY + 0.42, thePlayer.posZ)
                }
                "zonecraftnew" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z, true))
                    mc.thePlayer!!.posY = thePlayer.posY + 0.42
                }
                "ncp" -> {
                    if (!thePlayer.onGround)
                        return@run

                    for (i in 0..64) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.049, z, false))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z, false))
                    }

                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.1, z, true))

                    thePlayer.motionX *= 0.1
                    thePlayer.motionZ *= 0.1
                    thePlayer.swingItem()
                }
                "oldncp" -> {
                    if (!thePlayer.onGround)
                        return@run

                    for (i in 0..3) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 1.01, z, false))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z, false))
                    }

                    thePlayer.jump()
                    thePlayer.swingItem()
                }
                "bugspartan" -> {
                    for (i in 0..64) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.049, z, false))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z, false))
                    }

                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.1, z, true))

                    thePlayer.motionX *= 0.1
                    thePlayer.motionZ *= 0.1
                    thePlayer.swingItem()
                }
                "infinitycubecraft" -> ClientUtils.displayChatMessage("8[clCubeCraft-alFly8] aPlace a block before landing.")
                "infinityvcubecraft" -> {
                    ClientUtils.displayChatMessage("8[clCubeCraft-alFly8] aPlace a block before landing.")

                    thePlayer.setPosition(thePlayer.posX, thePlayer.posY + 2, thePlayer.posZ)
                }
                "boosthypixel" -> {
                    if (!thePlayer.onGround)
                        return@run

                    for (i in 0..9) {
                        //Imagine flagging to NCP.
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ, true))
                    }

                    var fallDistance = 3.0125 //add 0.0125 to ensure we get the fall dmg

                    while (fallDistance > 0) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 0.0624986421, thePlayer.posZ, false))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 0.0625, thePlayer.posZ, false))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 0.0624986421, thePlayer.posZ, false))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 0.0000013579, thePlayer.posZ, false))
                        fallDistance -= 0.0624986421
                    }

                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ, true))

                    thePlayer.jump()

                    thePlayer.posY += 0.42f // Visual
                    boostHypixelState = 1
                    moveSpeed = 0.1
                    lastDistance = 0.0
                    failedStart = false
                }
            }
        }

        startY = thePlayer.posY
        aacJump = -3.8
        noPacketModify = false
        if (mode.equals("freehypixel", ignoreCase = true)) {
            freeHypixelTimer.reset()
            thePlayer.setPositionAndUpdate(thePlayer.posX, thePlayer.posY + 0.42, thePlayer.posZ)
            freeHypixelYaw = thePlayer.rotationYaw
            freeHypixelPitch = thePlayer.rotationPitch
        }

        super.onEnable()
    }

    override fun onDisable() {

        if (mc.thePlayer == null || mc.theWorld == null) return
        mc.timer.timerSpeed = 1f
        mc.thePlayer!!.motionX = 0.0
        mc.thePlayer!!.motionZ = 0.0

        wasDead = false

        val thePlayer = mc.thePlayer ?: return

        noFlag = false

        val mode = modeValue.get()

        if (!mode.toUpperCase().startsWith("AAC") && !mode.equals("Hypixel", ignoreCase = true) &&
            !mode.equals("CubeCraft", ignoreCase = true)) {
            thePlayer.motionX = 0.0
            thePlayer.motionY = 0.0
            thePlayer.motionZ = 0.0
        }

        thePlayer.capabilities.isFlying = false

        mc.timer.timerSpeed = 1f
        thePlayer.speedInAir = 0.02f
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val vanillaSpeed = vanillaSpeedValue.get()
        val thePlayer = mc.thePlayer!!

        run {
            when (modeValue.get().toLowerCase()) {
                "matrixlastestboat" -> {
                    if (thePlayer.isRiding) {
                        jump = 1
                        lol = 0
                    } else {
                        if (jump == 1) {
                            jump = 0
                            lol = 1
                            mc.timer.timerSpeed = matrixTimer.get()
                            mc.thePlayer!!.motionY = matrixY.get().toDouble()
                            MovementUtils.strafe(matrixSpeed.get())
                            if (lol == 1) {
                                lol = 0
                            }
                        }
                    }
                }
                "test" -> {
                    if (mc.thePlayer!!.onGround) {
                        jump = 1
                        lol = 0
                        mc.thePlayer!!.posY = thePlayer.posY + 0.42
                    } else {
                        if (jump == 1) {
                            jump = 0
                            mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))
                            mc.timer.timerSpeed = 1f
                            mc.thePlayer!!.motionY = 0.0
                            MovementUtils.strafe(1.5f)
                        }
                    }
                }
                "oldredeshit" -> {
                    if (mc.thePlayer!!.isRiding) {
                        jump = 1
                        lol = 0
                    } else {
                        if (jump == 1) {
                            jump = 0
                            mc.timer.timerSpeed = 1f
                            mc.thePlayer!!.motionY = 40.0
                        }
                    }
                }
                "vanilla" -> {
                    thePlayer.capabilities.isFlying = false
                    thePlayer.motionY = 0.0
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                    if (mc.gameSettings.keyBindJump.isKeyDown) thePlayer.motionY += vanillaSpeed
                    if (mc.gameSettings.keyBindSneak.isKeyDown) thePlayer.motionY -= vanillaSpeed
                    MovementUtils.strafe(vanillaSpeed)
                    MovementUtils.strafe(vanillaSpeed)
                }
                "antikick" -> {
                    thePlayer.capabilities.isFlying = false
                    thePlayer.motionY = 0.0
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                    if (mc.gameSettings.keyBindJump.isKeyDown) thePlayer.motionY += vanillaSpeed
                    if (mc.gameSettings.keyBindSneak.isKeyDown) thePlayer.motionY -= vanillaSpeed
                    MovementUtils.strafe(vanillaSpeed)
                    MovementUtils.strafe(vanillaSpeed)
                    handleVanillaKickBypass()
                }
                "verus1" -> {
                    val verusfast = VerusfastspeedValue.get()
                    thePlayer.capabilities.isFlying = false
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))
                    thePlayer.motionY = 0.0
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                    if (mc.gameSettings.keyBindJump.isKeyDown) {
                        thePlayer.posY += 3.0
                    }
                    if (mc.gameSettings.keyBindSneak.isKeyDown) {
                        thePlayer.posY -= 3.0
                    }
                    MovementUtils.strafe(verusfast)
                }
                "vulcan" -> {
                    thePlayer.capabilities.isFlying = false
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))
                    thePlayer.motionY = 0.0
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                    if (mc.gameSettings.keyBindJump.isKeyDown) {
                        thePlayer.posY += 0.5
                        flyTimer2.reset()
                    }
                    if (mc.gameSettings.keyBindSneak.isKeyDown) {
                        thePlayer.posY -= 0.5
                        flyTimer2.reset()
                    }
                    if(mc.gameSettings.keyBindForward.isKeyDown){
                    if(flyTimer.hasTimePassed(100L)){
                        val yaw = Math.toRadians(mc.thePlayer!!.rotationYaw.toDouble())
                        val x1 = -Math.sin(yaw) * 7
                        val z1 = Math.cos(yaw) * 7
                        mc.thePlayer!!.setPosition(
                            mc.thePlayer!!.posX + x1,
                            mc.thePlayer!!.posY,
                            mc.thePlayer!!.posZ + z1
                        )
                        flyTimer.reset()
                        }
                    }
                }
                "verus2" -> {
                    val verusfast = VerusfastspeedValue.get()
                    thePlayer.capabilities.isFlying = false
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))
                    thePlayer.motionY = 0.0
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                    if(mc.gameSettings.keyBindForward.isKeyDown){
                    if(flyTimer.hasTimePassed(100L)) {
                        val yaw = Math.toRadians(mc.thePlayer!!.rotationYaw.toDouble())
                        val x1 = -Math.sin(yaw) * 8
                        val z1 = Math.cos(yaw) * 8
                        mc.thePlayer!!.setPosition(
                            mc.thePlayer!!.posX + x1,
                            mc.thePlayer!!.posY,
                            mc.thePlayer!!.posZ + z1
                        )
                        flyTimer.reset()
                        }
                    }
                    if (mc.gameSettings.keyBindJump.isKeyDown) {
                        mc.thePlayer!!.setPosition(mc.thePlayer!!.posX, mc.thePlayer!!.posY + 3, mc.thePlayer!!.posZ)
                        mc.timer.timerSpeed = 0.1f
                        thePlayer.posY += 3
                    }
                    if (mc.gameSettings.keyBindSneak.isKeyDown) {
                        mc.timer.timerSpeed = 0.1f
                        thePlayer.posY -= 3
                        flyTimer2.reset()
                    }
                    mc.timer.timerSpeed = 0.3f
                    MovementUtils.strafe(verusfast * 3)
                    flyTimer2.reset()
                }
                "creative" -> {
                    thePlayer.jump()
                    thePlayer.capabilities.isFlying = true
                    handleVanillaKickBypass()
                }
                "aac1.9.10" -> {
                    if (mc.gameSettings.keyBindJump.isKeyDown)
                        aacJump += 0.2
                    if (mc.gameSettings.keyBindSneak.isKeyDown)
                        aacJump -= 0.2

                    if (startY + aacJump > thePlayer.posY) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))
                        thePlayer.motionY = 0.8
                        MovementUtils.strafe(aacSpeedValue.get())
                    }
                    MovementUtils.strafe()
                }
                "aac3.0.5" -> {
                    if (aac3delay == 2) thePlayer.motionY = 0.1 else if (aac3delay > 2)
                        aac3delay = 0
                    if (aacFast.get()) {
                        if (thePlayer.movementInput.moveStrafe == 0.0f)
                            thePlayer.jumpMovementFactor = 0.08f
                        else
                            thePlayer.jumpMovementFactor = 0f
                    }
                    aac3delay++
                }
                "flag" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(thePlayer.posX + thePlayer.motionX * 999, thePlayer.posY + (if (mc.gameSettings.keyBindJump.isKeyDown) 1.5624 else 0.00000001) - if (mc.gameSettings.keyBindSneak.isKeyDown) 0.0624 else 0.00000002, thePlayer.posZ + thePlayer.motionZ * 999, thePlayer.rotationYaw, thePlayer.rotationPitch, true))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(thePlayer.posX + thePlayer.motionX * 999, thePlayer.posY - 6969, thePlayer.posZ + thePlayer.motionZ * 999, thePlayer.rotationYaw, thePlayer.rotationPitch, true))
                    thePlayer.setPosition(thePlayer.posX + thePlayer.motionX * 11, thePlayer.posY, thePlayer.posZ + thePlayer.motionZ * 11)
                    thePlayer.motionY = 0.0
                }
                "keepalive" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketKeepAlive())
                    thePlayer.capabilities.isFlying = false
                    thePlayer.motionY = 0.0
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                    if (mc.gameSettings.keyBindJump.isKeyDown) thePlayer.motionY += vanillaSpeed
                    if (mc.gameSettings.keyBindSneak.isKeyDown) thePlayer.motionY -= vanillaSpeed
                    MovementUtils.strafe(vanillaSpeed)
                }
                "spartan" -> {
                    thePlayer.motionY = 0.0

                    spartanTimer.update()
                    if (spartanTimer.hasTimePassed(12)) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 8, thePlayer.posZ, true))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY - 8, thePlayer.posZ, true))
                        spartanTimer.reset()
                    }
                }
                "neruxvace" -> {
                    if (!thePlayer.onGround) aac3glideDelay++
                    if (aac3glideDelay >= neruxVaceTicks.get() && !thePlayer.onGround) {
                        aac3glideDelay = 0
                        thePlayer.motionY = .015
                    }
                }
                "hypixel" -> {
                    val boostDelay = hypixelBoostDelay.get()
                    if (hypixelBoost.get() && !flyTimer.hasTimePassed(boostDelay.toLong())) {
                        mc.timer.timerSpeed = 1f + hypixelBoostTimer.get() * (flyTimer.hasTimeLeft(boostDelay.toLong()).toFloat() / boostDelay.toFloat())
                    }
                    hypixelTimer.update()
                    if (hypixelTimer.hasTimePassed(2)) {
                        thePlayer.setPosition(thePlayer.posX, thePlayer.posY + 1.0E-5, thePlayer.posZ)
                        hypixelTimer.reset()
                    }
                }
                "freehypixel" -> {
                    if (freeHypixelTimer.hasTimePassed(10)) {
                        thePlayer.capabilities.isFlying = true
                        return@run
                    } else {
                        thePlayer.rotationYaw = freeHypixelYaw
                        thePlayer.rotationPitch = freeHypixelPitch
                        thePlayer.motionY = 0.0
                        thePlayer.motionZ = thePlayer.motionY
                        thePlayer.motionX = thePlayer.motionZ
                    }
                    if (startY == BigDecimal(thePlayer.posY).setScale(3, RoundingMode.HALF_DOWN).toDouble()) freeHypixelTimer.update()
                }
                "bugspartan" -> {
                    thePlayer.capabilities.isFlying = false
                    thePlayer.motionY = 0.0
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                    if (mc.gameSettings.keyBindJump.isKeyDown)
                        thePlayer.motionY += vanillaSpeed
                    if (mc.gameSettings.keyBindSneak.isKeyDown)
                        thePlayer.motionY -= vanillaSpeed

                    MovementUtils.strafe(vanillaSpeed)
                }
            }
        }
    }
    @EventTarget
    fun onMotion(event: MotionEvent) {
            if (modeValue.get().equals("boosthypixel", ignoreCase = true)) {
                when (event.eventState) {
                    EventState.PRE -> {
                        hypixelTimer.update()
                        if (hypixelTimer.hasTimePassed(2)) {
                            mc.thePlayer!!.setPosition(
                                mc.thePlayer!!.posX,
                                mc.thePlayer!!.posY + 1.0E-5,
                                mc.thePlayer!!.posZ
                            )
                            hypixelTimer.reset()
                        }
                        if (!failedStart) mc.thePlayer!!.motionY = 0.0
                    }
                    EventState.POST -> {
                        val xDist = mc.thePlayer!!.posX - mc.thePlayer!!.prevPosX
                        val zDist = mc.thePlayer!!.posZ - mc.thePlayer!!.prevPosZ
                        lastDistance = sqrt(xDist * xDist + zDist * zDist)
                    }
                }
            }
        }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        val mode = modeValue.get()
        if (!markValue.get() || mode.equals("Vanilla", ignoreCase = true) || mode.equals("SmoothVanilla", ignoreCase = true)) return
        val y = startY + 2.0
        RenderUtils.drawPlatform(y, if (mc.thePlayer!!.entityBoundingBox.maxY < y) Color(0, 255, 255, 90) else Color(255, 0, 0, 90), 1.0)
        when (mode.toLowerCase()) {
            "aac1.9.10" -> RenderUtils.drawPlatform(startY + aacJump, Color(0, 0, 255, 90), 1.0)
            "hive" -> RenderUtils.drawPlatform(-70.0, Color(0, 255, 0, 90), 1.0)
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (noPacketModify) return

        if (classProvider.isCPacketPlayer(event.packet)) {

            val mode = modeValue.get()

            if (mode.equals("NCP", ignoreCase = true) || mode.equals("Rewinside", ignoreCase = true)||
                mode.equals("Mineplex", ignoreCase = true))
                if (mode.equals("Hypixel", ignoreCase = true)){

                }
        }
        if (classProvider.isSPacketPlayerPosLook(event.packet)) {
            val mode = modeValue.get()
            if (mode.equals("BoostHypixel", ignoreCase = true)) {
                failedStart = true
                ClientUtils.displayChatMessage("8[clBoostHypixel-alFly8] cSetback detected.")
            }
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        when (modeValue.get().toLowerCase()) {
            "cubecraft" -> {
                val yaw = Math.toRadians(mc.thePlayer!!.rotationYaw.toDouble())
                if (cubecraftTeleportTickTimer.hasTimePassed(2)) {
                    event.x = -sin(yaw) * 2.4
                    event.z = cos(yaw) * 2.4
                    cubecraftTeleportTickTimer.reset()
                } else {
                    event.x = -sin(yaw) * 0.2
                    event.z = cos(yaw) * 0.2
                }
            }
            "boosthypixel" -> {
                if (!MovementUtils.isMoving) {
                    event.x = 0.0
                    event.z = 0.0
                    return
                }
                if (failedStart)
                    return

                val amplifier = 1 + (if (mc.thePlayer!!.isPotionActive(classProvider.getPotionEnum(PotionType.MOVE_SPEED))) 0.2 *
                        (mc.thePlayer!!.getActivePotionEffect(classProvider.getPotionEnum(PotionType.MOVE_SPEED))!!.amplifier + 1.0) else 0.0)

                val baseSpeed = 0.29 * amplifier

                when (boostHypixelState) {
                    1 -> {
                        moveSpeed = (if (mc.thePlayer!!.isPotionActive(classProvider.getPotionEnum(PotionType.MOVE_SPEED))) 1.56 else 2.034) * baseSpeed
                        boostHypixelState = 2
                    }
                    2 -> {
                        moveSpeed *= 2.16
                        boostHypixelState = 3
                    }
                    3 -> {
                        moveSpeed = lastDistance - (if (mc.thePlayer!!.ticksExisted % 2 == 0) 0.0103 else 0.0123) * (lastDistance - baseSpeed)
                        boostHypixelState = 4
                    }
                    else -> moveSpeed = lastDistance - lastDistance / 159.8
                }

                moveSpeed = max(moveSpeed, 0.3)

                val yaw = MovementUtils.direction

                event.x = -sin(yaw) * moveSpeed
                event.z = cos(yaw) * moveSpeed

                mc.thePlayer!!.motionX = event.x
                mc.thePlayer!!.motionZ = event.z
            }
            "freehypixel" -> if (!freeHypixelTimer.hasTimePassed(10)) event.zero()
        }
    }

    @EventTarget
    fun onBB(event: BlockBBEvent) {
        if (mc.thePlayer == null) return
        val mode = modeValue.get()
        if (classProvider.isBlockAir(event.block) && (mode.equals("Hypixel", ignoreCase = true) ||
                    mode.equals("BoostHypixel", ignoreCase = true) || mode.equals("Rewinside", ignoreCase = true) ||
                    mode.equals("Mineplex", ignoreCase = true) && mc.thePlayer!!.inventory.getCurrentItemInHand() == null) && event.y < mc.thePlayer!!.posY) event.boundingBox = classProvider.createAxisAlignedBB(event.x.toDouble(), event.y.toDouble(), event.z.toDouble(), event.x + 1.0, mc.thePlayer!!.posY, event.z + 1.0)
    }

    @EventTarget
    fun onJump(e: JumpEvent) {
        val mode = modeValue.get()
        if (mode.equals("Hypixel", ignoreCase = true) || mode.equals("BoostHypixel", ignoreCase = true) ||
            mode.equals("Rewinside", ignoreCase = true) || mode.equals("Mineplex", ignoreCase = true)  && mc.thePlayer!!.inventory.getCurrentItemInHand() == null) e.cancelEvent()
    }

    @EventTarget
    fun onStep(e: StepEvent) {
        val mode = modeValue.get()
        if (mode.equals("Hypixel", ignoreCase = true) || mode.equals("BoostHypixel", ignoreCase = true) ||
            mode.equals("Rewinside", ignoreCase = true) || mode.equals("Mineplex", ignoreCase = true) && mc.thePlayer!!.inventory.getCurrentItemInHand() == null) e.stepHeight = 0f
    }

    private fun handleVanillaKickBypass() {
        if (!groundTimer.hasTimePassed(1000)) return
        val ground = calculateGround()
        run {
            var posY = mc.thePlayer!!.posY
            while (posY > ground) {
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, posY, mc.thePlayer!!.posZ, true))
                if (posY - 8.0 < ground) break // Prevent next step
                posY -= 8.0
            }
        }
        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, ground, mc.thePlayer!!.posZ, true))
        var posY = ground
        while (posY < mc.thePlayer!!.posY) {
            mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, posY, mc.thePlayer!!.posZ, true))
            if (posY + 8.0 > mc.thePlayer!!.posY) break // Prevent next step
            posY += 8.0
        }
        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ, true))
        groundTimer.reset()
    }

    // TODO: Make better and faster calculation lol
    private fun calculateGround(): Double {
        val playerBoundingBox: IAxisAlignedBB = mc.thePlayer!!.entityBoundingBox
        var blockHeight = 1.0
        var ground = mc.thePlayer!!.posY
        while (ground > 0.0) {
            val customBox = classProvider.createAxisAlignedBB(playerBoundingBox.maxX, ground + blockHeight, playerBoundingBox.maxZ, playerBoundingBox.minX, ground, playerBoundingBox.minZ)
            if (mc.theWorld!!.checkBlockCollision(customBox)) {
                if (blockHeight <= 0.05) return ground + blockHeight
                ground += blockHeight
                blockHeight = 0.05
            }
            ground -= blockHeight
        }
        return 0.0
    }

    override val tag: String
        get() = modeValue.get()
}