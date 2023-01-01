package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer

class VulcanGround : SpeedMode("VulcanGround") {
    var Timer = MSTimer()
    var timer2 = MSTimer()
    val playergay = mc.thePlayer
    override fun onMotion() {
        if(MovementUtils.isMoving){
            if(playergay!!.onGround){
                mc.timer.timerSpeed = 2f
                if(Timer.hasTimePassed(3000L)){
                    mc.timer.timerSpeed = 1f
                    if(timer2.hasTimePassed(1000L)){
                        mc.timer.timerSpeed = 2f
                    }
                }
            }
        }
    }

    override fun onUpdate() {}
    override fun onMove(event: MoveEvent) {}
}