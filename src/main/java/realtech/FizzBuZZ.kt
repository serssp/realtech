package realtech

import java.lang.StringBuilder
import java.util.concurrent.CyclicBarrier

class FizzBuZZ(private val max: Int) {

	@Volatile private var counter: Long = 1
	private val result = StringBuilder()
	private val barrier = CyclicBarrier(4) { counter++ }

	fun generate(): String {
		val threads = listOf(
				startThread({ "[FizzBuzz]" }) { counter % 3 == 0L && counter % 5 == 0L },
				startThread({ "[Fizz]" }) { counter % 3 == 0L && counter % 5 != 0L },
				startThread({ "[Buzz]" }) { counter % 3 != 0L && counter % 5 == 0L },
				startThread({ "[$it]" }) { counter % 3 != 0L && counter % 5 != 0L }
		)
		threads.forEach { it.join() }
		return result.toString()
	}

	private fun startThread(string: (Long) -> String, condition: (Long) -> Boolean): Thread {
		val thread = Thread {
			while (counter <= max) {
				if (condition(counter)) {
					write(string(counter))
				}
				barrier.await()
			}
		}
		thread.start()
		return thread
	}

	@Synchronized private fun write(str: String) {
		result.append(str)
	}
}
