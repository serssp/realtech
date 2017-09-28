package realtech

import org.testng.Assert.assertTrue
import org.testng.annotations.Test
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Test
class ExecutorTest {

	@Test(timeOut = 5000)
	fun test() {
		val bucket: MutableSet<Int> = Collections.newSetFromMap(ConcurrentHashMap())
		val executor = Executor<Int>(128, 4) {
			bucket.add(it)
		}

		val count = 10000
		val producer = Thread {
			repeat(count) { executor.add(it) }
		}
		producer.start()
		producer.join()
		while (bucket.size != count) {
			Thread.sleep(10)
		}

		repeat(count) { assertTrue(bucket.contains(it)) }
	}
}