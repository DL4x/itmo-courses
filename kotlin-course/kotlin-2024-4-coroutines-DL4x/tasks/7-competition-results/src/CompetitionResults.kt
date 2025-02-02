import kotlin.time.Duration
import kotlinx.coroutines.flow.*

fun Flow<Cutoff>.resultsFlow(): Flow<Results> {
    return scan(emptyMap<Int, Duration>()) { accumulator, cutoff ->
        accumulator + (cutoff.number to cutoff.time)
    }
        .drop(1)
        .map { Results(it) }
}

fun Flow<Results>.scoreboard(): Flow<Scoreboard> {
    return map {
        it.results.entries
            .sortedBy { (_, time) -> time }
            .mapIndexed { index, (number, time) ->
                ScoreboardRow(index + 1, number, time)
            }
    }
        .map { Scoreboard(it) }
}
