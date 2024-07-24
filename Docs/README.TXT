Library Catalog Program
By Dan Grace, Seng Heng, and Jian Ziong Lu

The library program represents a traditional lending library system, in which users can brows a library catalog, view their outstanding loans, check books for availability and check out and return books. 
Librarians can view all loans outstanding, add and remove new books, add and remove copies of those books, add and remove users, and other typical Librarian functions. 

The system includes a social media environment in which users can interact regarding their books and book preferences. 

The book schema includes Book records and Copy records. Books are a metaphysical representation of a book's attributes, a copy is a physical book. This enables the library to contain many copies of a single book. Users will be expected to brows the book library, find a book they like, then list the copies of that book. If they find a copy that is available, they can then check that book out by making the appropriate menu entry and entering the copy ID. 

When a librarian receives a new physical book. If it is a new copy of a book already in the system, they can look up the Book ID from the book list, then create a copy of that book, by selecting the menu item to create a new book copy, and entering the Book ID. If the book is entirely new to the library, the Librarian should first create a new book, then review the book list to obtain that book's id, then they can use that Book ID to create a new copy of that book through the menu interface. 

Deletion of book and copy records work similarly. As with all related records with a parent-child relationship, copies will be deleted if their parent Book entry is deleted. This deletion logic is handled by the database. 

The system can be run by compiling and executing the LibraryProgram.java file. 

The system comes with a pre-loaded database in the root file at Library.db. In the event of corruption or if the user would like to restore the environment, a backed up database can be found in Data\Library.db.restore. 

Additional documentation can be found here in the Docs folder including UML diagrams, testing overview, and in-depth testing scripts. 
Annotated javadocs describing class interfaces and usage can be found in javadocs\



A video demonstration of the Library functionality can be found here: 
https://youtu.be/vj46kvcS8XQ







Figma Flowchart. 

https://www.figma.com/board/V6ulCOdmxVshy1cHv0COqZ/CSUEB-Software-Engineering-Library-Setup?node-id=0-1&t=2g6tmBTidB98Yz4q-0