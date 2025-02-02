const val MILLISECONDS_IN_SECOND: Int = 1_000
const val SECONDS_IN_MINUTE: Int = 60
const val MINUTES_IN_HOUR: Int = 60

private fun convertTimeToMilliseconds(time: Time): Long {
    return time.seconds * MILLISECONDS_IN_SECOND + time.milliseconds
}

private fun convertMillisecondsToTime(milliseconds: Long): Time {
    return Time(
        milliseconds / MILLISECONDS_IN_SECOND,
        (milliseconds % MILLISECONDS_IN_SECOND).toInt(),
    )
}

val Int.milliseconds: Time
    get() = convertMillisecondsToTime(this.toLong())

val Int.seconds: Time
    get() = this.milliseconds * MILLISECONDS_IN_SECOND

val Int.minutes: Time
    get() = this.seconds * SECONDS_IN_MINUTE

val Int.hours: Time
    get() = this.minutes * MINUTES_IN_HOUR

private fun timeOperation(
    first: Time,
    second: Time,
    operation: (Long, Long) -> Long,
): Time {
    val result = operation(
        convertTimeToMilliseconds(first),
        convertTimeToMilliseconds(second),
    )
    return convertMillisecondsToTime(result)
}

operator fun Time.plus(other: Time): Time {
    return timeOperation(this, other, Long::plus)
}

operator fun Time.minus(other: Time): Time {
    return timeOperation(this, other, Long::minus)
}

operator fun Time.times(times: Int): Time {
    return convertMillisecondsToTime(convertTimeToMilliseconds(this) * times)
}
