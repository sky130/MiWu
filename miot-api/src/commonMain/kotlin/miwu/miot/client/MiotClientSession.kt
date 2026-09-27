package miwu.miot.client

/**
 * A set of MIoT clients that share one authenticated transport.
 *
 * The session owns the transport. Close the session when the user changes or signs out; the
 * individual clients are non-owning views and may be reused for the session lifetime.
 */
interface MiotClientSession : AutoCloseable {
    val homeClient: MiotHomeClient
    val userClient: MiotUserClient
    val deviceClient: MiotDeviceClient
}
