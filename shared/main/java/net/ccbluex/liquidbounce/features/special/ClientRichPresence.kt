package net.ccbluex.liquidbounce.features.special

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.IPCListener
import com.jagrosh.discordipc.entities.RichPresence
import com.jagrosh.discordipc.entities.pipe.PipeStatus
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.render.DiscordRPC
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import org.json.JSONObject
import java.io.IOException
import java.time.OffsetDateTime
import kotlin.concurrent.thread

class ClientRichPresence : MinecraftInstance() {

    var showRichPresenceValue = true

    // IPC Client
    private var ipcClient: IPCClient? = null

    private var appID = 0L
    private val assets = mutableMapOf<String, String>()
    private val timestamp = OffsetDateTime.now()

    // Status of running
    private var running: Boolean = false

    /**
     * Setup Discord RPC
     */
    fun setup() {
        try {
            running = true

            loadConfiguration()

            ipcClient = IPCClient(appID)
            ipcClient?.setListener(object : IPCListener {

                /**
                 * Fired whenever an [IPCClient] is ready and connected to Discord.
                 *
                 * @param client The now ready IPCClient.
                 */
                override fun onReady(client: IPCClient?) {
                    thread {
                        while (running) {
                            update()

                            try {
                                Thread.sleep(1000L)
                            } catch (ignored: InterruptedException) {
                            }
                        }
                    }
                }

                /**
                 * Fired whenever an [IPCClient] has closed.
                 *
                 * @param client The now closed IPCClient.
                 * @param json A [JSONObject] with close data.
                 */
                override fun onClose(client: IPCClient?, json: JSONObject?) {
                    running = false
                }

            })
            ipcClient?.connect()
        } catch (e: Throwable) {
            ClientUtils.getLogger().error("Failed to setup Discord RPC.", e)
        }

    }

    /**
     * Update rich presence
     */
    fun update() {
        val builder = RichPresence.Builder()

        // Set playing time
        builder.setStartTimestamp(timestamp)

        // Check assets contains logo and set logo
        if (assets.containsKey("logo"))
            builder.setLargeImage(assets["logo"], "I love this pvp mod")

        // Check user is ingame
        if (mc.thePlayer != null) {

            // Set display infos
            builder.setDetails("Username: " + mc.session.username) 
            builder.setState("In game health: " + mc.thePlayer!!.health)
        }

        // Check ipc client is connected and send rpc
        if (ipcClient?.status == PipeStatus.CONNECTED)
            ipcClient?.sendRichPresence(builder.build())
    }

    /**
     * Shutdown ipc client
     */
    fun shutdown() {
        if (ipcClient?.status != PipeStatus.CONNECTED) {
            return
        }

        try {
            ipcClient?.close()
        } catch (e: Throwable) {
            ClientUtils.getLogger().error("Failed to close Discord RPC.", e)
        }
    }

    /**
     * Load configuration from web
     *
     * @throws IOException If reading failed
     */
    private fun loadConfiguration() {
        // Read from web and convert to json object
        val aha = LiquidBounce.CLIENT_WEBSITE
        val json = JsonParser().parse(HttpUtils.get(aha + "LiquidBounce/discord.json"))

        if (json !is JsonObject)
            return

        // Check has app id
        if (json.has("appID"))
            appID = json.get("appID").asLong

        // Import all asset names
        for ((key, value) in json.get("assets").asJsonObject.entrySet())
            assets[key] = value.asString
    }
}