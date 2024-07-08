package mate.academy;

import java.math.BigDecimal;
import mate.academy.model.Book;
import mate.academy.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book().builder()
                    .title("Book Title")
                    .author("Author")
                    .isbn("ISBN")
                    .price(BigDecimal.valueOf(9.99))
                    .description("Book Description")
                    .coverImage("cover.jpg")
                    .build();
            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}
