package realtech

import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.RingBuffer
import com.lmax.disruptor.WorkHandler
import com.lmax.disruptor.dsl.Disruptor
import java.util.concurrent.Executors

/*
Реализуйте потоки производителя (Producer) и потребителя (Consumer), совместно пользующихся буфером фиксированного размера.
Первый поток должен помещать числа в буфер в бесконечном цикле, а второй — бесконечно извлекать их оттуда. Порядок добавления и извлечения чисел не имеет значения.
Данные производителя не должны теряться: либо считаться потребителем, либо остаться в буфере.
Решение по организации ожидания чтения, в случае пустого буфера, или записи, в случае заполненного буфера, остается за вами.
*/

/**
 * Вариантов много, классика, я думаю, это ArrayBlockingQueue
 *
 * [bufferSize] Размер очереди, должен быть степенью двойки
 */
class Executor<T : Any>(bufferSize: Int,
                        threadsCount: Int,
                        worker: (T) -> Unit) {

	private val ringBuffer: RingBuffer<Task<T>>

	init {
		val disruptor = Disruptor(EventFactory { Task<T>() }, bufferSize, Executors.newCachedThreadPool())
		disruptor.handleEventsWithWorkerPool(*Array<WorkHandler<Task<T>>>(threadsCount) {
			WorkHandler {
				worker(it.payload)
			}
		})
		ringBuffer = disruptor.start()
	}

	fun add(task: T) {
		val sequence = ringBuffer.next()
		val t = ringBuffer.get(sequence)
		t.payload = task
		ringBuffer.publish(sequence)
	}
}

class Task<T : Any> {
	lateinit var payload: T
}