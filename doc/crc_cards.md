### Prompt
*As an owner, I want to add a book in my books, each denoted with a clear, suitable description (at least title, author, and ISBN).*  
*As an owner, I want the book description by scanning it off the book (at least the ISBN).*
*As an owner or borrower, I want a book to have a status to be one of: available, requested, accepted, or borrowed.*
*As an owner, I want to view a list of all my books, and their descriptions, statuses, and current borrowers.*
*As an owner, I want to view a list of all my books, filtered by status.*
*As an owner, I want to view and edit a book description in my books.*
*As an owner, I want to delete a book in my books.*

## Book ##
Responsibilities | Collaborators 
-----------------|---------------
A book has a title, author and ISBN | Owner
Keeps track of its own status| Borrowing
Has a description that's accessible through the ISBN | Scan
A book can be borrowed or requested by a borrower | 
A book accepted for borrowing by the owner| 
A book can be availbale for borrowing| 
A book's description can be edited| 


### Prompt
*As an owner or borrower, I want a profile with a unique username and my contact information.*  
*As an owner or borrower, I want to edit the contact information in my profile.*
*As an owner or borrower, I want to retrieve and show the profile of a presented username.*

## User Profile ##
Responsibilities | Collaborators 
-----------------|---------------
Has a user name| User
Has contact information that can be edited| 
Can be retrieved by the user |


### Prompt 

## User ##
Responsibilities | Collaborators
-----------------|---------------
Has username  | Owner
Has password | Borrower
Has email | 
Has phone number| 
Has list of owned books | 
Has list of borrowed books |


### Prompt
*As an owner, I want to add a book in my books, each denoted with a clear, suitable description (at least title, author, and ISBN).*  
*As an owner, I want the book description by scanning it off the book (at least the ISBN).*
*As an owner or borrower, I want a book to have a status to be one of: available, requested, accepted, or borrowed.*
*As an owner, I want to view a list of all my books, and their descriptions, statuses, and current borrowers.*
*As an owner, I want to view a list of all my books, filtered by status.*
*As an owner, I want to view and edit a book description in my books.*
*As an owner, I want to delete a book in my books.*

## Owner Extends User ##
Responsibilities | Collaborators 
-----------------|---------------
Adds a Book to personal Book list | Book
Edits a Book in personal Book list| Status
Deletes a Book from personal Book list | Scan
Views personal Book list | User
Views personal Book list, filtered by Status | Lends
Scans a Book's ISBN | Request
Has User profile with unique username and contact info | Geolocation
Edits contact info in User profile | Photograph
Views other Users' profiles (from their presented username) |
Views all Requests on owned Books |
Gets notified of new Request on an owned Book |
Accepts a Request on an owned Book | 
Specifies the Geolocation of where an owned Book should be picked up | 
Declines a Request on an owned Book |
Lends out an owned Book |
Receive an owned Book that has been returned |
Attach a Photograph to an owned Book | 
Deletes an attached Photograph from an owned Book |
Views an attached Photograph on any Book | 


### Prompt 
*As an owner, I want to hand over a book by scanning the book ISBN code and denoting the book as borrowed.*
*As a borrower, I want to receive an accepted book by scanning the book ISBN code to confirm I have borrowed it.*
*As a borrower, I want to view a list of books I am borrowing, each book with its description and owner username. *

## Borrower ##
Responsibilities | Collaborators
-----------------|---------------
Views every book with a tpye of status  | Book
Has a unique username and contact info | Status
Ability to edit ones contact info | Scan*
Access to search and view username profiles | Status*
Custom Keyword search of all books (Not Borrowed/Accepted)* | Availablity*
Search results to display description, owner username and status  | Requesting
View a list of books that one has requested that displays it's description, owner username and status| User
Request boooks that are not currently accepted or borrwed | Geolocation
Updated/Notified of accepted request | Searching*
View a list of each book that one has requested and have been accepted. Each display description, owner username and status| Accepting
Scan the accepted book's ISBN Code to confirm that it's been recieved | Borrowing
View a list of each book that one is currently borrowing. Each displaying it's description, owner username and status| Photographs
Request for a book to be retruned, by scanning the book's ISBN code and marking it as avalible | Location
View the image/photograph for the book | 
Be notified//specified on where to recieve the book to be borrowed | 


### Prompt
*As a borrower, I want to specify a keyword, and search for all books that are not currently accepted or borrowed whose description contains the keyword.*  
*As a borrower, I want search results to show each book with its description, owner username, and status.*

## Searching ##
Responsibilities | Collaborators 
-----------------|---------------
Knows all the books containing the keyword that are not accepted | Accepting
Knows all the books containing the keyword in that are not borrowed | Borrowing
Knows all the books that contain keyword in description | Borrower 
Show the description, username of the book's owner and status details for each book | Books
Knows all the books containing the keyword that are not accepted or borrowed (pending accept from the owner)*| Users
  

### Prompt 
*As a borrower, I want to request a book that is not currently accepted or borrowed.*
As a borrower, I want to view a list of books I have requested, each book with its description, owner username, and status.*
*As an owner, I want to be notified of a request.
*As an owner, I want to view all the requests on one of my books.*  

## Requesting ##
Responsibilities | Collaborators
-----------------|---------------
Knows all the books that are available (not accepted/borrowed) | Borrower
Make a request for a book that is available | Accepting
Send a request to the book's owner | Book
Keep track of the books that the borrower has requested in the past | Borrowing*
Shows a list of the available book's description, owner's username and status | Users
Send a notification to the available book's owner of the request  | 
Show owener all requests made on an ones own book | 


### Prompt 
*As an owner, I want to accept a request on one of my books. (Any other requests on the book are declined).*
*As an owner, I want to decline a request on one of my books.*
*As a borrower, I want to be notified of an accepted request.*
*As a borrower, I want to view a list of books I have requested that are accepted, each book with its description, and owner username.* 

## Accepting ##
Responsibilities | Collaborators
-----------------|---------------
Accepts a book request | Owner
Prevents further requests on accepted requests*| Borrower
Declines book request | Book
Notify/Update borrower when book status hass been accepted | 
View list of each book requested and accepted with their description and username | 


### Prompt 
*As an owner, I want to hand over a book by scanning the book ISBN code and denoting the book as borrowed.*
*As a borrower, I want to receive an accepted book by scanning the book ISBN code to confirm I have borrowed it.*
*As a borrower, I want to view a list of books I am borrowing, each book with its description and owner username.*

## Borrowing ##
Responsibilities | Collaborators
-----------------|---------------
Denotes book as borrowed | Owner
Confirms book reception | Borrower
Views borrowed books with description and owner username | Book
Scans book ISBN | Scan
Allow the borrower to view the recieving location of the request book| 


### Prompt 
*As a borrower, I want to hand over a book I borrowed by scanning the book ISBN code to denote the book as available.*
*As an owner, I want to receive a returned book by scanning the book ISBN code to confirm I have it available.*

## Returning 
Responsibilities | Collaborators
-----------------|---------------
Request for a book to be retruned  | Borrower
Recieve request for book to be returned | Owner
Scan a book's ISBN Code	 | Book
Request for book to be available| Status*
Confirm the book avaliblity| Scan*
 | Availablity
 | Requesting
 | User
 | Geolocation


### Prompt 
*As an owner, I want to optionally attach a photograph to a book of mine.*
*As an owner, I want to delete any attached photograph for a book of mine.*
*As an owner or borrower, I want to view any attached photograph for a book.*

## Photographs 
Responsibilities | Collaborators
-----------------|---------------
Allow the owner to upload a photograph for the book | Book
Allow the owner to delete a photograph for the book | Owner
Allow the owner/borrower to view the photograph for the book | Borrower


### Prompt 
*As an owner, I want to specify a geo location on a map of where to receive a book when I accept a request on the book.*
*As a borrower, I want to view the geo location of where to receive a book I will be borrowing.*

## Location 
Responsibilities | Collaborators
-----------------|---------------
Allow the owner to choose a location on the map | Owner
Allow the owner to accept a request on the book | Book
Allow the owner to recieve the accepted book at the specified location they chose| Accepting
Allow the borrower to borrow a book| Requesting
Allow the borrower to view the recieving location of the request book| Borrower
 | Borrowing
