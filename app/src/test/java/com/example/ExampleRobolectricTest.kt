package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AlphabetRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ABC Kids Game", appName)
  }

  @Test
  fun `verify full alphabet repository contains 26 letters`() {
    assertEquals(26, AlphabetRepository.alphabetList.size)
    val first = AlphabetRepository.getByLetter('A')
    assertNotNull(first)
    assertEquals("Apple", first?.word)
    val last = AlphabetRepository.getByLetter('Z')
    assertNotNull(last)
    assertEquals("Zebra", last?.word)
  }
}

