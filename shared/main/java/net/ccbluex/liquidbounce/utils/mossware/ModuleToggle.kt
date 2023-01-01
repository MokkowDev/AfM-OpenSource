package net.ccbluex.liquidbounce.utils.mossware

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.FileUtils
import java.io.File

class ModuleToggle {
    var enableSound : ModuleSound
    var disableSound : ModuleSound

    init {
        val enableSoundFile=File(LiquidBounce.fileManager.soundDir,"enable.wav")
        val disableSoundFile=File(LiquidBounce.fileManager.soundDir,"disable.wav")

        FileUtils.unpackFile(enableSoundFile,"sounds/enablejello.wav")
        FileUtils.unpackFile(disableSoundFile,"sounds/disablejello.wav")

        enableSound=ModuleSound(enableSoundFile)
        disableSound=ModuleSound(disableSoundFile)
    }
}