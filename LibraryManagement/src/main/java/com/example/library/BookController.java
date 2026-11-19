
package com.example.library;

import java.util.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@RestController
@RequestMapping("/books")
public class BookController {

    HashMap<Integer, Book> books = new HashMap<>();

    // A. ADD NEW BOOK

    @PostMapping
    public ResponseEntity<String> addBook(
            @RequestBody Book book) {

        if (books.containsKey(book.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Book already exists!");
        }

        books.put(book.getId(), book);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Book added successfully!");
    }

    // B. GET ALL BOOKS

    @GetMapping
    public List<Book> getAllBooks() {

        return new ArrayList<>(books.values());
    }

    // C. GET BOOK BY ID

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(
            @PathVariable Integer id) {

        Book book = books.get(id);

        if (book != null) {
            return ResponseEntity.ok(book);
        }

        return ResponseEntity.notFound().build();
    }

    // D. UPDATE BOOK

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(
            @PathVariable Integer id,
            @RequestBody Book newBook) {

        Book book = books.get(id);

        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        book.setTitle(newBook.getTitle());
        book.setAuthor(newBook.getAuthor());
        book.setCategory(newBook.getCategory());
        book.setAvailableCopies(newBook.getAvailableCopies());

        return ResponseEntity.ok("Book updated successfully!");
    }

    // E. DELETE BOOK

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(
            @PathVariable Integer id) {

        if (!books.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        books.remove(id);

        return ResponseEntity.ok("Book deleted successfully!");
    }
}
