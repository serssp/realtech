package realtech

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/*
Экземпляр класса передается 3 потокам: ThreadA вызывает first(), ThreadB вызывает second(), ThreadC вызывает third().
Разработайте механизм, гарантирующий, что метод first() будет вызван перед second(), а метод second() будет вызван перед third().
 */

interface Foo {
	fun first()
	fun second()
	fun third()
}

class ThreadABC(private val foo: Foo) {
	fun start() {
		val lock = ReentrantLock()
		val init = lock.newCondition()
		val first = lock.newCondition()
		val second = lock.newCondition()
		val third = lock.newCondition()

		val b0 = AtomicBoolean(true)
		val ba = AtomicBoolean()
		val bb = AtomicBoolean()

		val threadA = PipedExecutor(b0, lock, init, first) { ba.set(true); foo.first() }
		val threadB = PipedExecutor(ba, lock, first, second) { bb.set(true); foo.second() }
		val threadC = PipedExecutor(bb, lock, second, third) { foo.third() }

		lock.withLock {
			init.signal()
		}

		threadA.start()
		threadB.start()
		threadC.start()

		threadA.join()
		threadB.join()
		threadC.join()
	}
}

class PipedExecutor(
		private val prevExecuted: AtomicBoolean,
		private val lock: ReentrantLock,
		private val inbound: Condition,
		private val outbound: Condition,
		private val action: () -> Unit
) : Thread() {
	override fun run() {
		lock.withLock {
			while (!prevExecuted.get()) {
				inbound.await()
			}
			action()
			outbound.signal()
		}
	}
}
