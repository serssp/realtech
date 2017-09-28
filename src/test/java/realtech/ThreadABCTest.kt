package realtech

import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import org.testng.annotations.Test

class ThreadABCTest {

	@Test(invocationCount = 1000)
	fun test() {
		val foo = mock<Foo>()

		ThreadABC(foo).start()

		inOrder(foo) {
			verify(foo).first()
			verify(foo).second()
			verify(foo).third()
		}
	}
}