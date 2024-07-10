package library.utilities;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import library.model.*;
import library.db.*;

public class DataLoader {

    public static void loadBooksFromCSV(Book_Access bookAccess, String csvFilePath) throws IOException, SQLException {
        try (Reader reader = new FileReader(csvFilePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                String isbn = csvRecord.get("ISBN");
                String title = csvRecord.get("Title");
                String author = csvRecord.get("Author");

                Book book = new Book();
                book.setISBN(isbn);
                book.setTitle(title);
                book.setAuthor(author);

                bookAccess.insert(book);
            }
        }
    }
}
