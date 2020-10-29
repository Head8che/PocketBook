package com.example.pocketbook.util;

/**
 * Parser class to validate all text
 *  before adding it to Firestore
 */
public class Parser {

    String title, author, isbn, comment;

    public Parser(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
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

}
