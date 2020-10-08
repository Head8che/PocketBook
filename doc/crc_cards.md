### Prompt
*As a borrower, I want to specify a keyword, and search for all books that are not currently accepted or borrowed whose description contains the keyword.*  

*As a borrower, I want search results to show each book with its description, owner username, and status.*

## Searching 
Responsibilities | Collaborators 
-----------------|---------------
knows all the books containing the keyword that are not accepted | Accepting
knows all the books containing the keyword in that are not borrowed | Borrowing
knows all the books that contain keyword in description | Borrower 
show the description, username of the book's owner, status as details for each book | Books
knows all the books containing the keyword that are not accepted or borrowed (pending accept from the owner)| Users
  

### Prompt 
*As a borrower, I want to request a book that is not currently accepted or borrowed.
As a borrower, I want to view a list of books I have requested, each book with its description, owner username, and status.*


*As an owner, I want to be notified of a request.
As an owner, I want to view all the requests on one of my books.*  

  
  
## Requesting 
Responsibilities | Collaborators
-----------------|---------------
knows all the books that are available (not accepted/borrowed) | Borrower
make a request for a book that is available | Accepting
send a request to the book's owner | Books
keep track of the books that the borrower has requested in the past | Borrowing
shows a list of the available book's description, owner's username, status | Users
send a notification to the available book's owner of the request  | 
show all the requests made on an owner's requested book | 





