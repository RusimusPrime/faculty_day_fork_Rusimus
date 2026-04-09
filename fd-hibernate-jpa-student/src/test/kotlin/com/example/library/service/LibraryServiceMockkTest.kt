package com.example.library.service
import com.example.library.entity.Author
import com.example.library.entity.Book
import com.example.library.entity.Genre
import com.example.library.repository.AuthorRepository
import com.example.library.repository.BookRepository
import com.example.library.repository.GenreRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.Optional

class LibraryServiceMockkTest {

    private val authorRepository = mockk<AuthorRepository>()
    private val bookRepository = mockk<BookRepository>()
    private val genreRepository = mockk<GenreRepository>()

    private lateinit var service: LibraryService

    @BeforeEach
    fun setUp() {
        service = LibraryService(authorRepository, bookRepository, genreRepository)
    }

    @Test
    fun `createAuthor возвращает того же автора что вернул save`() {
        val inputAuthor = Author(name = "John Doe")
        val savedAuthor = Author(id = 42L, name = "John Doe")
        every { authorRepository.save(eq(inputAuthor)) } returns savedAuthor
        val result = service.createAuthor("John Doe")
        assertEquals(42L, result.id)
        assertEquals("John Doe", result.name)
        verify(exactly = 1) { authorRepository.save(eq(inputAuthor)) }
    }

    @Test
    fun `getAllGenres возвращает список из genreRepository findAll`() {
        val genres = listOf(
            Genre(id = 1L, name = "Fantasy"),
            Genre(id = 2L, name = "Science Fiction")
        )
        every { genreRepository.findAll() } returns genres
        val result = service.getAllGenres()
        assertEquals(2, result.size)
        assertEquals("Fantasy", result[0].name)
        verify(exactly = 1) { genreRepository.findAll() }
    }

    @Test
    fun `createBook бросает EntityNotFoundException если автор не найден`() {
        val authorId = 99L
        every { authorRepository.findById(eq(authorId)) } returns Optional.empty()
        val exception = assertThrows(EntityNotFoundException::class.java) {
            service.createBook("Some Title", "123-456", authorId, 1L)
        }
        assertEquals("Author not found with id: $authorId", exception.message)
        verify(exactly = 1) { authorRepository.findById(eq(authorId)) }
        verify(exactly = 0) { bookRepository.save(any()) }
    }

    @Test
    fun `createBook передаёт в save книгу с нужным названием и ISBN`() {
        val author = Author(id = 1L, name = "Author")
        every { authorRepository.findById(eq(1L)) } returns Optional.of(author)
        val bookSlot = slot<Book>()
        every { bookRepository.save(capture(bookSlot)) } answers {
            bookSlot.captured.copy(id = 100L)
        }
        val result = service.createBook("Test Book", "ISBN-001", 1L, 2L)
        assertEquals(100L, result.id)
        assertEquals("Test Book", bookSlot.captured.title)
        assertEquals("ISBN-001", bookSlot.captured.isbn)
        assertEquals(author, bookSlot.captured.author)
        verify(exactly = 1) {
            bookRepository.save(match { book ->
                book.title == "Test Book" && book.isbn == "ISBN-001"
            })
        }
    }

    @Test
    fun `getBooksPage делегирует в bookRepository findAll с постраничностью`() {
        val author = Author(id = 1L, name = "Author")
        val book = Book(id = 10L, title = "Example", isbn = "123", author = author)
        val pageRequest = PageRequest.of(0, 20, Sort.by("title"))
        val page = PageImpl(listOf(book), pageRequest, 1)
        every { bookRepository.findAll(eq(pageRequest)) } returns page
        val result = service.getBooksPage(page = 0, size = 20)
        assertEquals(1, result.content.size)
        assertEquals("Example", result.content[0].title)
        verify(exactly = 1) { bookRepository.findAll(eq(pageRequest)) }
    }
}