package lib.utilities;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import lib.db.*;
import lib.model.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

public class DataLoader {

    public static void loadBooksFromCSV(Book_Access bookAccess, String csvFilePath) throws IOException, SQLException {
        try (Reader reader = new FileReader(csvFilePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                String isbn = csvRecord.get("ISBN");
                String title = csvRecord.get("Title");
                String author = csvRecord.get("Author");

                Book book = Book.create();
                book.setISBN(isbn);
                book.setTitle(title);
                book.setAuthor(author);

                bookAccess.insert(book);
            }
        }
    }
}
