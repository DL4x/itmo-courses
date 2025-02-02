fun sendMessageToClient(
    client: Client?,
    message: String?,
    mailer: Mailer,
) {
    val email = client?.personalInfo?.email
    email?.let { mailer.sendMessage(it, message ?: "Hello!") }
}
