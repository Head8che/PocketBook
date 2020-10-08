## Returning 
-------------------------------------------------------------
|  Returning                                | Collaborators   |
|  -----------------------------------------|---------------- |
|  Responsibilities:                        | Borrower        |
|  Request for a book to be retruned        | Owner           |
|  Recieve request for book to be returned  | Book            |
|  Scan a book's ISBN Code	 		  | Scan            |
|  Request for book to be available 	  | Status          |
|  Confirm the book avaliblity      	  | Availablity     |
|  				     	              | Request         |
|  				     	              | User 	        |
|  				     	              | Geolocation	  |
--------------------------------------------------------------


-------------------------------------------------------------
|  Returning                                        	       |
|  ----------------------------------------------------------|
|  Variables                                                 |
|                                                   	       |
|                                                   	       |
|                                                   	       |
|                                                   	       |
|  Methods                                          	       |
|                                                   	       |
|                                                   	       |
|                                                   	       |
|                                                   	       |
|                                                   	       |
-------------------------------------------------------------


## Borrower 
--------------------------------------------------------------------------------------------------------------------------------------------------
|  Borrower extends User                                                                                                        | Collaborators   |
|  ---------------------------------------------------------------------------------------------------------------------------- |-----------------|
|  Responsibilities:                                                                                                            | Book            |
|  Views every book with a tpye of status                                                                                       | Status          |
|  Has a unique username and contact info                                                                                       | Scan            |
|  Ability to edit ones contact info                                                                                            | Status          |
|  Access to search and view username profiles                                                                                  | Availablity     |
|  Custom Keyword search of all books (Not Borrowed/Accepted)*                                                                  | Request         |
|  Search results to display description, owner username and status                                                             | User            |
|  View a list of books that one has requested that displays it's description, owner username and status                        | Geolocation     |
|  Request boooks that are not currently accepted or borrwed 				     	                                      | Searching       |
|  Updated/Notified of accepted request                     				     	                                      | Accepting       |
|  View a list of each book that one has requested and have been accepted. Each display description, owner username and status  | Accepting       |
|  Scan the accepted book's ISBN Code to confirm that it's been recieved. 				     	                          | Borrowing       |
|  View a list of each book that one is currently borrowing. Each displaying it's description, owner username and status  	  | Borrowing       |
|  Request for a book to be retruned, by scanning the book's ISBN code and marking it as avalible  				     	  | Photographs     |
|  View the image/photograph for the book 				     	                                                        | Location        |
|  Be notified//specified on where to recieve the book to be borrowed				     	                                |                 |
--------------------------------------------------------------------------------------------------------------------------------------------------


-------------------------------------------------------------
|  Borrower                                        	       |
|  ----------------------------------------------------------|
|  Variables                                                 |
|                                                   	       |
|                                                   	       |
|                                                   	       |
|                                                   	       |
|  Methods                                          	       |
|                                                   	       |
|                                                   	       |
|                                                   	       |
|                                                   	       |
|                                                   	       |
-------------------------------------------------------------