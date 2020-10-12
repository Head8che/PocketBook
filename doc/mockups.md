# Login Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/01_login_page.jpg" alt="Login Page" width=300 /> | **ACTIONS**<br />- Click on the username field to ENTER username<br />- Click on the password field to ENTER password<br />- Click on the **Log In Button** to log in<br />- Click on the **Sign Up Link** to go to the Sign Up page<br /><br/>**MOVERS**<br />- **Log In**: <br />    - validate form:<br />        - IF valid GOTO [Home Page All Tab](#home-page-all-tab)<br />          ELSE repeat [Login Page](#login-page)<br />- **Sign Up**: <br />    - GOTO [Sign Up Page](#sign-up-page)<br /><br /> |


# Sign Up Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/02_signup_page.jpg" alt="Sign Up Page" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to Login Page<br />- Click on the **Picture Circle** to add a picture [OPTIONAL?]<br />- Click on the First Name field to ENTER first name<br />- Click on the Last Name field to ENTER last name<br />- Click on the Email field to ENTER email<br />- Click on the Username field to ENTER username<br />- Click on the Password field to ENTER password<br />- Click on the Retype Password field to RETYPE password<br />- Click on the **Sign Up Button** to go to the Home page<br /><br/>**MOVERS**<br />- **Back**: <br />    - RETURN TO [Login Page](#login-page)<br />- **Picture**: <br />    - LAUNCH [Add Image Fragment](#add-image-fragment)<br />- **Sign Up**: <br />    - validate form:<br />        - IF valid GOTO [Home Page All Tab](#home-page-all-tab)<br />          ELSE repeat [Sign Up Page](#sign-up-page)<br /><br /> |


# POCKETBOOK FOOTER

| **Screen Mockup**                                 | **Description**                                              |
| ------------------------------------------------- | ------------------------------------------------------------ |
| The Footer at the bottom of subsequent activities | - **POCKETBOOK FOOTER**: <br />    - GOTO [Home Page](#home-page-all-tab),<br />    - GOTO [Search Page](#search-page),<br />    - GOTO [Add Book Fragment](#add-book-fragment),<br />    - GOTO [Scan](#scan)<br />    - GOTO [User Profile Owner Page](#user-profile-owner-page)<br /><br /> |


# Home Page All Tab

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/03_home_page.jpg" alt="Home Page" width=300 /> | **ACTIONS**<br />- Click on the **Notification Icon** to view notifications<br />- Click on the All Tab to view all books in the catalogue<br />- Click on the Available Tab to view available books in the catalogue<br />- Click on a **Book** to go to the Book Page<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br/>**MOVERS**<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **All Tab**: <br />    - STAY ON [Home Page All Tab](#home-page-all-tab)<br />- **Available Tab**: <br />    - GOTO [Home Page Available Tab](#home-page-available-tab)<br />- **Book**: <br />    - check if user owns book:<br />        - IF owner <br />            IF book is available GOTO [Owner Book Page](#owner-book-page), <br />            ELSE GOTO [Owner Book Page Not Available](#owner-book-page-not-available)<br />          ELSE GOTO [Non Owner Book Page](#non-owner-book-page)<br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Non Owner Book Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/04_non_owner_book_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click on the **Owner's Icon/Username** to view the owner's profile<br />- Click on the **Request Button** to request the book<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br/>- **Request**: <br />    - SEND request<br />    - NOTIFY Owner <br />    - CHANGE book status from AVAILABLE to REQUESTED<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Owner's Icon/Username**: <br />    - GOTO [View Owner Page](#view-owner-page)<br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Non Owner Book Page Not Available

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/04A_non_owner_book_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Notification Icon** to view notifications<br />- Click on the **Owner's Icon/Username** to view the owner's profile<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Owner's Icon/Username**: <br />    - GOTO [View Owner Page](#view-owner-page)<br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Owner Book Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| Request List should not be on this page!<br /><img src="Mockups/05_owner_book_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click on the Book Tab to view the selected book<br />- Click on the Requests Tab to view the book's requests<br />- Click on the **Delete Button** to remove the book<br />- Click on the **Edit Button** to edit the book<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Book Tab**: <br />    - STAY ON [Owner Book Page](#owner-book-page)<br />- **Requests Tab**: <br />    - GOTO [Owner Book Page Requests](#owner-book-page-requests)<br />- **Delete**: <br />    - LAUNCH [Delete Fragment](#delete-fragment)<br />      IF successful delete GOTO *Previous Activity*<br />      ELSE STAY ON [Owner Book Page](#owner-book-page)<br />- **Edit**: <br />    - GOTO [Edit Book Page](#edit-book-page)<br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Owner Book Page Requests

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/05A_owner_book_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click on the Book Tab to view the selected book<br />- Click on the Requests Tab to view the book's requests<br />- Click on the **Owner's Icon/Username** to view the owner's profile<br />- Click on the **Request Action Button** to accept/decline the book's requests<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Book Tab**: <br />    - GOTO [Owner Book Page](#owner-book-page)<br />- **Owner's Icon/Username**: <br />    - GOTO [View Owner Page](#view-owner-page)<br />- **Requests Tab**: <br />    - STAY ON [Owner Book Page Requests](#owner-book-page-requests)<br />- **Request Action**: <br />    - LAUNCH [Request Fragment](#request-fragment)<br /><br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# View Owner Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/06_view_owner_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click on a **Book** to go to the Book Page<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Book**: <br />    - check if user owns book:<br />        - IF owner <br />            IF book is available GOTO [Owner Book Page](#owner-book-page), <br />            ELSE GOTO [Owner Book Page Not Available](#owner-book-page-not-available)<br />          ELSE GOTO [Non Owner Book Page](#non-owner-book-page)<br /><br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Add Book Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/08_add_book_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click on the **Picture Rectangle** to add a picture [OPTIONAL?]<br />- Click on the Title field to ENTER title<br />- Click on the Author field to ENTER author<br />- Click on the ISBN field to ENTER ISBN manually<br />- Click on the **Scan Icon** to scan an ISBN<br />- Click on the Comment field to ENTER comment<br />- Click on the **Add Book Button** to add the book<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Picture**: <br />    - LAUNCH [Add Image Fragment](#add-image-fragment)<br />- **Scan**: <br />    - GOTO [Scan](#scan)<br />- **Add**: <br />    - SAVE book (with available status); RETURN TO *Previous Activity*<br /><br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Edit Book Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/07_edit_book_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click on the **Picture Rectangle** to edit the picture [OPTIONAL?]<br />- Click on the Title field to MODIFY title<br />- Click on the Author field to MODIFY author<br />- Click on the ISBN field to MODIFY ISBN manually<br />- Click on the **Scan Icon** to scan an ISBN<br />- Click on the Comment field to MODIFY comment<br />- Click on the **Save Changes Button** to save the book edits<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Picture**: <br />    - LAUNCH [Edit Image Fragment](#add-image-fragment)<br />- **Scan**: <br />    - GOTO [Scan](#scan)<br />- **Save Changes**: <br />    - SAVE book edits; RETURN TO *Previous Activity*<br /><br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Scanned Book Description Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/10_scanned_book_description_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br /><br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |

# User Profile Owner Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/11_user_profile_page.jpg" alt="Home Page" width=300 /> | **ACTIONS**<br />- Click on the **Notification Icon** to view notifications<br />- Click on the **Edit Button** to edit the profile<br />- Click on the Owner Tab to view the books that you own<br />- Click on the Borrower Tab to view the books that others own<br />- Click on a **Book** to go to the Book Page<br />- Click on a **View All** section to view all the books in that section<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br/>**MOVERS**<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Edit**: <br />    - GOTO [Edit Profile Page](#edit-profile-page)<br />- **Owner Tab**: <br />    - STAY ON [User Profile Owner Page](#user-profile-owner-page)<br />- **Borrower Tab**: <br />    - GOTO [User Profile Borrower Tab](#user-profile-borrower-tab)<br />- **Book**: <br />    - IF book is available GOTO [Owner Book Page](#owner-book-page), <br />      ELSE GOTO [Owner Book Page Not Available](#owner-book-page-not-available)<br />- **View All**: <br />    - GOTO [Profile View All Page](#profile-view-all-page)<br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# User Profile Borrower Tab

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/11B_user_profile_page.jpg" alt="Home Page" width=300 /> | **ACTIONS**<br />- Click on the **Notification Icon** to view notifications<br />- Click on the **Edit Button** to edit the profile<br />- Click on the Owner Tab to view the books that you own<br />- Click on the Borrower Tab to view the books that others own<br />- Click on a **Book** to go to the Book Page<br />- Click on a **View All** section to view all the books in that section<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br/>**MOVERS**<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Edit**: <br />    - GOTO [Edit Profile Page](#edit-profile-page)<br />- **Owner Tab**: <br />    - GOTO [User Profile Owner Page](#user-profile-owner-page)<br />- **Borrower Tab**: <br />    - STAY ON [User Profile Borrower Tab](#user-profile-borrower-tab)<br />- **Book**: <br />    - GOTO [Non Owner Book Page](#non-owner-book-page)<br />- **View All**: <br />    - GOTO [Profile View All Page](#profile-view-all-page)<br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Edit Profile Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/12_edit_profile_page.jpg" alt="Sign Up Page" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Picture Circle** to edit a picture [OPTIONAL?]<br />- Click on the First Name field to MODIFY first name<br />- Click on the Last Name field to MODIFY last name<br />- Click on the Email field to MODIFY email<br />- Click on the Username field to MODIFY username<br />- Click on the Password field to MODIFY password<br />- Click on the Retype Password field to RETYPE password<br />- Click on the **Edit Profile Button** to edit the profile<br /><br/>**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Picture**: <br />    - LAUNCH [Edit Image Fragment](#edit-image-fragment)<br />- **Edit Profile**: <br />    - validate form:<br />        - IF valid GOTO [User Profile Owner Page](#user-profile-owner-page)<br />          ELSE repeat [Edit Profile Page](#edit-profile-page)<br /><br /> |


# Profile View All Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/13_profile_view_all_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click on the **Filter Icon** to filter the books by status<br />- Click on the **Search Bar** to search through the books<br />- Click on a **Book** to go to the Book Page<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Filter**: <br />    - LAUNCH [Filter Owned Books Fragment](#filter-owned-books-fragment)<br />- **Search**: <br />    - GOTO [Search Page](#search-page)<br />- **Book**: <br />    - check if user owns book:<br />        - IF owner <br />            IF book is available GOTO [Owner Book Page](#owner-book-page), <br />            ELSE GOTO [Owner Book Page Not Available](#owner-book-page-not-available)<br />          ELSE GOTO [Non Owner Book Page](#non-owner-book-page)<br /><br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Profile View All Page Requested

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/13A_profile_view_all_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click on the **Search Bar** to search through the books<br />- Click on a **Book** to go to the Book Page<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Search**: <br />    - GOTO [Search Page](#search-page)<br />- **Book**: <br />    - check if user owns book:<br />        - IF owner <br />            IF book is available GOTO [Owner Book Page](#owner-book-page), <br />            ELSE GOTO [Owner Book Page Not Available](#owner-book-page-not-available)<br />          ELSE GOTO [Non Owner Book Page](#non-owner-book-page)<br /><br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Search Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/14_search_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click on the Toggle button to toggle the search type<br />- Click on the **Search Bar** to search through the books<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Search**: <br />    - GOTO *Results Page*<br /><br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Notifications Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/15_notifications_page.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Owner's Icon/Username** to view the owner's profile<br />- Click on a **Book** (icons on the right) to go to the Book Page<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Owner's Icon/Username**: <br />    - GOTO [View Owner Page](#view-owner-page)<br />- **Book**: <br />    - check if user owns book:<br />        - IF owner <br />            IF book is available GOTO [Owner Book Page](#owner-book-page), <br />            ELSE GOTO [Owner Book Page Not Available](#owner-book-page-not-available)<br />          ELSE GOTO [Non Owner Book Page](#non-owner-book-page)<br /><br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Set Location Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/16_location_page.jpg" alt="Image Fragment" width=300 /> | The Map shows where the pickup location is.<br /><br />**ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click on the **Set Pickup Location Button** to set the location<br />- Click on the **Confirm Location Button** to confirm the chosen location<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **Set Pickup Location**: <br />    - GOTO [Google Maps](#google-maps) TO [Set Location Page](#set-location-page)<br />- **Confirm Location**: <br />    RETURN TO *Previous Activity*<br /><br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Get Location Page

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/16A_location_page.jpg" alt="Image Fragment" width=300 /> | The Map shows where the pickup location is.<br /><br />**ACTIONS**<br />- Click on the **Back Button** to return to *Previous Activity*<br />- Click on the **Notification Icon** to view notifications<br />- Click on the **ETA Location Button** to get directions to the location<br />- Click in the **POCKETBOOK FOOTER** to perform footer actions<br /><br />**MOVERS**<br />- **Back**: <br />    - RETURN TO *Previous Activity*<br />- **Notification**: <br />    - GOTO [Notifications Page](#notifications-page)<br />- **ETA Location**: <br />    - GOTO [Google Maps](#google-maps) TO [Get Location Page](#get-location-page)<br /><br />- [**POCKETBOOK FOOTER**](#pocketbook-footer): <br /><br /> |


# Edit Image Fragment

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/18_image_fragment.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Select Photo Button** to go to the Android Gallery<br />- Click on the **Take Photo Button** to go to the Camera App<br />- Click on the **Remove Photo Button** to return to the Sign Up Page<br /><br/>**MOVERS**<br />- **Select Photo**: <br />    - GOTO [Android Gallery](#android-gallery) TO [Confirm Photo Page](#confirm-photo-page) TO *Previous Activity*<br />- **Take Photo**: <br />    - GOTO [Camera App](#camera-app) TO [Confirm Photo Page](#confirm-photo-page) TO *Previous Activity*<br />- **Remove Photo**: <br />    - RETURN TO *Previous Activity* (after removing photo)<br /><br /> |


# Request Fragment

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/19_request_fragment.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Accept Request Button** to accept the request<br />- Click on the **Decline Request Button** to decline the request<br />- Click on the **Cancel Button** to exit the fragment<br /><br/>**MOVERS**<br />- **Accept Request**: <br />    - SEND accept request<br />    - NOTIFY Borrower that request is accepted<br />    - SEND decline request to other requesters<br />    - CHANGE book status from REQUESTED to ACCEPTED<br />    - RETURN to *Previous Activity* <br />- **Decline Request**: <br />    - SEND decline request<br />    - NOTIFY Borrower that request is declined<br />    - IF there are no other requests<br />         - CHANGE book status from REQUESTED to AVAILABLE<br />    - RETURN to *Previous Activity* <br />- **Cancel**: <br />    - RETURN to *Previous Activity* <br /><br /> |


# Delete Fragment

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/20_delete_fragment.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Yes Button** to confirm the deletion<br />- Click on the **Cancel Button** to exit the fragment<br /><br/>**MOVERS**<br />- **YES**: <br />    - DELETE the specified item<br />    - RETURN to *Previous Activity* <br />- **Cancel**: <br />    - RETURN to *Previous Activity* <br /><br /> |


# Filter Owned Books Fragment

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/21_filter_owned_books_fragment.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Ready for Pickup Button** to see ACCEPTED books<br />- Click on the **Requested Button** to see REQUESTED books<br />- Click on the **Borrowed Button** to see BORROWED books<br />- Click on the **Owned Button** to see OWNED books<br /><br/>**MOVERS**<br />- **Ready for Pickup**: <br />    - Include accepted books in [Profile View All Page](#profile-view-all-page) list<br />- **Requested**: <br />    - Include requested books in [Profile View All Page](#profile-view-all-page) list<br />- **Borrowed**: <br />    - Include borrowed books in [Profile View All Page](#profile-view-all-page) list<br />- **Owned**: <br />    - Include owned books in [Profile View All Page](#profile-view-all-page) list<br />- **Apply**: <br />    - RETURN to *Previous Activity* with filter options set<br /><br /> |


# Scan Book Fragment

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/22_scan_book_fragment.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **See Description Button** to see the scanned book's description<br />- Click on the **Add Book** to add the book<br />- Click on the **Lend Book Button** to lend the book<br />- Click on the **Receive Book Button** to receive the book<br />- Click on the **Return Book Button** to return the book<br /><br/>**MOVERS**<br />- **See Description**: <br />    - GOTO [Scanned Book Description Page](#scanned-book-description-page)<br />- **Add Book**: <br />    - GOTO [Add Book Page](#add-book-page) (and prefill fields)<br />- **Lend Book**: <br />    - CHANGE book status from ACCEPTED to BORROWED<br />    - RETURN to *Previous Activity*<br />- **Receive Book**: <br />    - CHANGE book status from BORROWED to AVAILABLE<br />    - RETURN to *Previous Activity*<br />- **Return Book**: <br />    - CHANGE book status from BORROWED to AVAILABLE<br />    - RETURN to *Previous Activity*<br /><br /> |


# Add Book Fragment

| **Screen Mockup**                                            | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="Mockups/23_add_book_fragment.jpg" alt="Image Fragment" width=300 /> | **ACTIONS**<br />- Click on the **Scan ISBN Button** to scan the book's ISBN<br />- Click on the **Enter Manually Button** to enter the book by hand<br />- Click on the **Cancel Button** to exit the fragment<br /><br/>**MOVERS**<br />- **Scan ISBN**: <br />    - GOTO [Scan](#scan) <br />- **Enter Manually**: <br />    - GOTO [Add Book Page](#add-book-page) <br />- **Cancel**: <br />    - RETURN to *Previous Activity* <br /><br /> |




# System Activities/Dialogs

## Android Gallery

| **From**                                  | **To**                                    |
| ----------------------------------------- | ----------------------------------------- |
| [Add Image Fragment](#add-image-fragment) | [Confirm Photo Page](#confirm-photo-page) |

## Camera App

| **From**                                  | **To**                                    |
| ----------------------------------------- | ----------------------------------------- |
| [Add Image Fragment](#add-image-fragment) | [Confirm Photo Page](#confirm-photo-page) |

## Scan

| **From**                                                     | **To**                         |
| ------------------------------------------------------------ | ------------------------------ |
| [Add Book Page](#add-book-page)<br />[Edit Book Page](#edit-book-page)<br />[Add Book Fragment](#add-book-fragment)<br />[**POCKETBOOK FOOTER**](#pocketbook-footer) | *Calling Activity or Fragment* |

## Notifications

| **From**           | **To**            |
| ------------------ | ----------------- |
| *Calling Activity* | *Result Activity* |

## Google Maps

| **From**                                                     | **To**                                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [Set Location Page](#set-location-page)<br />[Get Location Page](#get-location-page) | [Set Location Page](#set-location-page)<br />[Get Location Page](#get-location-page) |

