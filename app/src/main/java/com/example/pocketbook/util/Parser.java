package com.example.pocketbook.util;

/**
 * Parser class to validate all text
 *  before adding it to Firestore
 */
public class Parser {

    private String title;
    private String author;
    private String isbn;
    private String status;
    private String comment;
    private String ownerEmail;
    private String photo;
    private String condition;

    private String[] conditions;
    private String[] statuses;
    private String[] parsedArguments;



    public Parser(String title, String author, String isbn, String ownerEmail, String status,
                    String comment, String photo, String condition) {

        // initialize the permitted values for conditions and statuses
        setConditions();
        setStatuses();

        // trim all values
        this.title = title.trim();
        this.author = author.trim();
        this.isbn = isbn.trim();
        this.status = status.trim();
        this.ownerEmail = ownerEmail.trim();
        this.condition = condition; // set my drop down menu options
        this.comment = comment; // can be null
        this.photo = photo; // can be null
    }


    /**
     * Statuses Initializer
     */
    private void setStatuses() {
        this.statuses = new String[]
                {
                        "AVAILABLE",
                        "REQUESTED",
                        "BORROWED",
                        "ACCEPTED"
                };
    }

    /**
     * Conditions Initializer
     */
    private void setConditions() {
        this.conditions = new String[]
                {
                        "BRAND NEW",
                        "LIKE NEW",
                        "VERY GOOD",
                        "GOOD",
                        "ACCEPTABLE"
                };

    }

    /**
     * Checks if the status specified is in the list of statuses
     * @return
     *      true if status is in statuses
     *      false otherwise
     */
    private boolean checkStatus() {
        for (String s : statuses) {
            if (status.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the condition specified is in the list of conditions
     * @return
     *      true if condition is in conditions
     *      false otherwise
     */
    private boolean checkCondition() {
        for (String s : conditions) {
            if (condition.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check that the fields are not empty
     * @return
     *      true if length of title and author > 0
     *      false otherwise
     */
    public boolean checkTitleAndAuthor() {
        return title.length() > 0 && author.length() > 0;
    }

    /**
     * Only accept string with digits
     * if it is a valid isbn
     *
     * Resource :
     *  https://www.geeksforgeeks.org/program-check-isbn/#:~:text=To%20verify%20an%20ISBN%2C%20calculate,code%20is%20a%20valid%20ISBN.
     *
     * @return
     *      true if passes through all checks
     *      false otherwise
     */
    public boolean checkIsbn() {
        // length must be 10
        int n = isbn.length();
        if (n != 10)
            return false;

        // Computing weighted sum of first 9 digits
        int sum = 0;
        for (int i = 0; i < 9; i++)
        {
            int digit = isbn.charAt(i) - '0';
            if (0 > digit || 9 < digit)
                return false;
            sum += (digit * (10 - i));
        }

        // Checking last digit.
        char last = isbn.charAt(9);
        if (last != 'X' && (last < '0' ||
                last > '9'))
            return false;

        // If last digit is 'X', add 10
        // to sum, else add its value
        sum += ((last == 'X') ? 10 : (last - '0'));

        // Return true if weighted sum
        // of digits is divisible by 11.
        return (sum % 11 == 0);
    }

    /**
     * If the comment is null,
     * set the comment to be empty
     */
    public void setEmptyComment() {
        comment = "";
    }

    /**
     * All email should be lowercase
     */
    public void setLowerEmail() {
        ownerEmail.toLowerCase();
    }

    /**
     * If no photo is specified,
     * set it to an empty string
     */
    public void setEmptyPhoto() {
        photo = "";
    }

    /**
     * Add to a string Array
     * @param arg
     * @return
     */
    public String[] _addArgument(String arg) {
        // create a new array with increased length
        int oldLength = parsedArguments.length;
        String[] newArray = new String[oldLength + 1];
        // copy parsedArguments to newArray
        if (newArray.length >= 0)
            System.arraycopy(parsedArguments, 0, newArray, 0, newArray.length);

        // add new element to newArray
        newArray[oldLength] = arg;

        return newArray;
    }

    /**
     *
     */
    public void checkAttributes() {
        // minimum check
        if (checkTitleAndAuthor() &&
                checkIsbn()) {
            // add to parsedArguments
            parsedArguments = _addArgument(title);
            parsedArguments = _addArgument(author);
            parsedArguments = _addArgument(isbn);
        }
        // check the condition
        if (checkCondition()) {
            parsedArguments = _addArgument(condition);
        }
        else {
            condition = "N/A";
            parsedArguments = _addArgument(condition);
        }
        // check the status
        if (checkStatus()) {
            parsedArguments = _addArgument(status);
        }
        // check for photo
        if (photo.equals(null)) {
            setEmptyPhoto();
            parsedArguments = _addArgument(photo);
        }
        // check for comment
        if (comment.equals(null)) {
            setEmptyComment();
            parsedArguments = _addArgument(comment);
        }
    }

    /**
     * Returns the attributes after parsing
     * @return
     *      String array of parsed attributes
     */
    public String[] returnParsedArguments() {
        return parsedArguments;
    }
}
