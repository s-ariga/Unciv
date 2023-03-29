package com.unciv.logic.multiplayer.apiv2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/**
 * An update of the game data
 *
 * This variant is sent from the server to all accounts that are in the game.
 */
@Serializable
data class UpdateGameData(
    @SerialName("gameId")
    @Serializable(with = UUIDSerializer::class)
    val gameUUID: UUID,
    val gameData: String,  // base64-encoded, gzipped game state
    /** A counter that is incremented every time a new game states has been uploaded for the same [gameUUID] via HTTP API. */
    @SerialName("gameDataId")
    val gameDataID: Long
)

/**
 * Notification for clients if a client in their game disconnected
 */
@Serializable
data class ClientDisconnected(
    @SerialName("gameId")
    val gameID: Long,
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID  // client identifier
)

/**
 * Notification for clients if a client in their game reconnected
 */
@Serializable
data class ClientReconnected(
    @SerialName("gameId")
    val gameID: Long,
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID  // client identifier
)

/**
 * A new chat message is sent to the client
 */
@Serializable
data class IncomingChatMessage(
    @SerialName("chatId")
    val chatID: Long,
    val message: ChatMessage
)

/**
 * The base WebSocket message, encapsulating only the type of the message
 */
interface WebSocketMessage {
    val type: WebSocketMessageType
}

/**
 * Message when a previously sent WebSocket frame a received frame is invalid
 */
@Serializable
data class InvalidMessage(
    override val type: WebSocketMessageType,
) : WebSocketMessage

/**
 * Message to publish the new game state from the server to all clients
 */
@Serializable
data class UpdateGameDataMessage (
    override val type: WebSocketMessageType,
    val content: UpdateGameData
) : WebSocketMessage

/**
 * Message to indicate that a client disconnected
 */
@Serializable
data class ClientDisconnectedMessage (
    override val type: WebSocketMessageType,
    val content: ClientDisconnected
) : WebSocketMessage

/**
 * Message to indicate that a client, who previously disconnected, reconnected
 */
@Serializable
data class ClientReconnectedMessage (
    override val type: WebSocketMessageType,
    val content: ClientReconnected
) : WebSocketMessage

/**
 * Message to indicate that a user received a new text message via the chat feature
 */
@Serializable
data class IncomingChatMessageMessage (
    override val type: WebSocketMessageType,
    val content: IncomingChatMessage
) : WebSocketMessage

/**
 * Type enum of all known WebSocket messages
 */
@Serializable(with = WebSocketMessageTypeSerializer::class)
enum class WebSocketMessageType(val type: String) {
    InvalidMessage("invalidMessage"),
    UpdateGameData("updateGameData"),
    ClientDisconnected("clientDisconnected"),
    ClientReconnected("clientReconnected"),
    IncomingChatMessage("incomingChatMessage");

    companion object {
        private val VALUES = values()
        fun getByValue(type: String) = VALUES.first { it.type == type }
    }
}