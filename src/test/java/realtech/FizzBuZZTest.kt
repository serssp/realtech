package realtech

import org.testng.annotations.Test
import java.lang.StringBuilder
import kotlin.test.assertEquals

@Test
class FizzBuZZTest {

	fun test() {
		val max = 1000
		val generator = FizzBuZZ(max)

		val actual = generator.generate()
		val expected = genarateExpected(max)

		assertEquals(expected, actual)
	}

	fun genarateExpected(n: Int): String {
		val result = StringBuilder()
		for (i in 1..n) {
			if (i % 3 == 0 && i % 5 == 0) {
				result.append("[FizzBuzz]")
			} else if (i % 3 == 0) {
				result.append("[Fizz]")
			} else if (i % 5 == 0) {
				result.append("[Buzz]")
			} else {
				result.append("[$i]")
			}
		}
		return result.toString()
	}
}