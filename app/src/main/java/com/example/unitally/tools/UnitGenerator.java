package com.example.unitally.tools;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.example.unitally.objects.Unit;

/**
 * String example, 4oz water
 *                 8 windows
 *  4(worth) oz(symbol) water(unit)
 *  8(worth) windows(unit)
 *
 *  if three tokens then,
 *  number = worth
 *  token following number = symbol
 *  last token = unit, unit name
 *
 *
 *  if two tokens then,
 *  number = worth
 *  second token = unit, unit name
 *
 *  IMPLEMENTATION:
 *  - Need to pass parent unit
 *  - Need to pass definition string
 *
 *  Async task which will attach the generated units to passed unit
 *
 *
 *  POSSIBLE INPUT TYPES
 *  v8 6oz      **
 *  $6 Price    Should pass easily
 *  $5          Should pass easily
 *  5ibs        Should pass easily
 *  5ibs coffee Should pass easily
 *  coffee 5ibs Should pass easily
 *  8 oz coffee Should pass easily
 *  water 8 oz  Should pass easily
 *  water oz 8  **
 *
 */

public class UnitGenerator {

    private static final String[] IGNORED_WORDS = {"of"};

    private Unit mHostUnit;
    private double mWorth;
    private String mSymbol, mName;

    private String mUnit_Definition;

    public UnitGenerator(Unit hostUnit) {
        mHostUnit = hostUnit;

        mWorth = 0;
        mSymbol = null;
        mName = null;
    }

    public void define(String unit_def) {
        mUnit_Definition = unit_def;
    }

    private void generate() {
        // Tokenize into array

        // Identify worth
        // Reorder appropriately

        // Convert into Unit
        // Attach to host
    }

    private class StringTokenizerAsync extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... strings) {

            String unit_def = strings[0];
            String[] tokens = tokenize(unit_def);

            return new String[0];
        }

        // Assign to Host Unit
        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
        }

        /**
         * Separates the Unit definitions into tokens using whitespace as a delimiter.
         *
         * @param definition String entered by the user defining the Unit to be created
         * @return String array containing tokens
         */
        private String[] tokenize(String definition) {
            // "\\s" = white space character
            String[] tokens = definition.split("\\s");

            String[] valid_tokens = new String[3];

            for(String word:tokens) {
                if(isToken(word)) {
                    String[] stuff = identify(word);

                    if(stuff[TARGET_ID].equalsIgnoreCase(FOUND_NUMBER)) {
                        valid_tokens[P_WORTH] = stuff[TARGET_INFO];
                    }
                    else if(stuff[TARGET_ID].equalsIgnoreCase(FOUND_SYMBOL)) {
                        valid_tokens[P_SYMBOL] = stuff[TARGET_INFO];
                    }
                    else if(stuff[TARGET_ID].equalsIgnoreCase(FOUND_NAME)) {
                        valid_tokens[P_NAME] = stuff[TARGET_INFO];
                    }
                }
            }

            return valid_tokens;
        }

        private final String FOUND_NUMBER = "16253.example.unitally.FOUND A NUMBER";
        private final String FOUND_SYMBOL = "16253.example.unitally.FOUND SYMBOL";
        private final String FOUND_NAME = "16253.example.unitally.FOUND A UNIT NAME";

        private final int TARGET_INFO = 0,
                            TARGET_ID = 1,
                            AD_INFO = 2;

        /**
         *  This class should only return an Array with 1 or 2 in length
         *
         * @param token Word to be investigated
         * @return Array containing either value, symbol, value and symbol, or name
         *             Array[0] = Target info
         *             Array[1] = Target identifier
         *             Array[2] = Additional info (if any)
         */
        private String[] identify(@NonNull String token) {
            // Break token apart, if necessary
            String word = analyze(token);
            String[] words = word.split("\\s");

            // Only one token was passed back, could be word, number, or symbol
            if(words.length == 1) {

            }

            // Two tokens were passed back, could be number and word or number and symbol
            // or symbol and number
            else if(words.length == 2) {

            }
            else {
                throw new RuntimeException("WORDS WAS UNABLE TO PROCESS CORRECTLY");
            }
        }

        private final int P_WORTH  = 0,
                          P_SYMBOL  = 1,
                          P_NAME    = 2;

        /**
         * Analyzes the String parameter passed and adds a space between any words and numbers.
         *
         * @param word Token with no spaces
         * @return String with numerical values separated.
         */
        private String analyze (String word) {
            // Try and parse the entire word into number
            char a;
            Integer state = null;
            StringBuilder portion = new StringBuilder();

            for(int i=0; i<word.length(); i++) {
                a=word.charAt(i);

                // Try to identify into number
                if(isNumber(a)) {
                    // Assign initial state
                    if(state == null) {
                        state = P_WORTH;
                        portion.append(a);
                    }
                    else {
                        // Append normally
                        if (state == P_WORTH) {
                            portion.append(a);

                        }
                        // Came from a different state, separate word
                        else {
                            if(!portion.toString().contains(" ")) {
                                state = P_WORTH;
                                portion.append(" ");
                                portion.append(a);
                            }
                            // If a space was found then the word has already alternated,
                            // therefore input was something like: "6oz9"
                            else
                                return word;
                        }
                    }
                }

                // Try to identify symbol
                else {
                    // Assign initial state
                    if(state == null) {
                        state = P_SYMBOL;
                        portion.append(a);
                    }
                    else {
                        // Append normally
                        if (state == P_SYMBOL) {
                            portion.append(a);

                        }
                        // Came from a different state, separate word
                        else {
                            if(!portion.toString().contains(" ")) {
                                state = P_SYMBOL;
                                portion.append(" ");
                                portion.append(a);
                            }
                            // If a space was found then the word has already alternated,
                            // therefore input was something like: "6oz9"
                            else
                                return word;
                        }
                    }
                }
            }

            return portion.toString();
        }

        private boolean isNumber(char character) {
            if(character == '-' || character == '.') {
                return true;
            }
            else {
                try {
                    Integer.parseInt(String.valueOf(character));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }

        private String[] tryNumber(String word) {
            try {
                Double.parseDouble(word);
                return null;
            }catch(NumberFormatException e) {
                return null;
            }
        }

        // **NOTES
        //      Numbers surrounded by numbers is a name
        //      if a two tokens are found with numbers, make
        //          the token with less characters the number
        //      the other will be the name
    }

    private String[] organize(String[] tokens, String identifier) {
        // Found the target plus additional info
        if(tokens[1] != null) {
            String[] identifiers = new String[3];
            identifiers[0] = tokens[0]; // Assign target
            identifiers[2] = tokens[1]; // Additional info, Treat as symbol or number

            // Notify what this package contains
            identifiers[1] = identifier;
            return identifiers;
        }

        // Only if target was found
        else {
            String[] identifiers = new String[2];
            identifiers[0] = tokens[0]; // Assign target

            // Notify what this package contains
            identifiers[1] = identifier;
            return identifiers;
        }
    }

    /**
     * When generating units using a String definition, this method will
     * check if the passed word is to be ignored.
     *
     * Example: Unit Definition = "4 oz of coffee", ignore "of"
     * @param questioned_word
     * @return
     */
    private static boolean isToken(@NonNull String questioned_word) {
        for(String word:IGNORED_WORDS) {
            if(word.equalsIgnoreCase(questioned_word))
                return false;
        }
        return true;
    }
}
