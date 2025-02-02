/**
 * @author Shulpin Egor
 */
class Solution : MonotonicClock {
    private var c1 by RegularInt(0)
    private var c2 by RegularInt(0)
    private var c3 by RegularInt(0)

    private var c4 by RegularInt(0)
    private var c5 by RegularInt(0)
    private var c6 by RegularInt(0)

    override fun write(time: Time) {
        // c2: write left-to-right
        c4 = time.d1
        c5 = time.d2
        c6 = time.d3
        // c1: write right-to-left
        c3 = time.d3
        c2 = time.d2
        c1 = time.d1
    }

    override fun read(): Time {
        // c1: read left-to-right
        val u1 = c1
        val u2 = c2
        val u3 = c3
        val r1 = Time(u1, u2, u3)
        // c2: read right-to-left
        val v1 = c6
        val v2 = c5
        val v3 = c4
        val r2 = Time(v3, v2, v1)

        if (r1 == r2) return r1

        if (r1.d1 != r2.d1) {
            return Time(r2.d1, 0, 0)
        }
        if (r1.d2 != r2.d2) {
            return Time(r2.d1, r2.d2, 0)
        }
        return Time(r2.d1, r2.d2, r2.d3)
    }
}
