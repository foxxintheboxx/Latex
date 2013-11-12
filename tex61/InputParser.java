package tex61;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.MatchResult;
import java.io.Reader;

import static tex61.FormatException.reportError;

/** Reads commands and text from an input source and send the results
 *  to a designated Controller. This essentially breaks the input down
 *  into "tokens"---commands and pieces of text.
 *  @author Ian Fox
 */
class InputParser {

    /** Matches text between { } in a command, including the last
     *  }, but not the opening {.  When matched, group 1 is the matched
     *  text.  Always matches at least one character against a non-empty
     *  string or input source. If it matches and group 1 is null, the
     *  argument was not well-formed (the final } was missing or the
     *  argument list was nested too deeply). */
    private static final Pattern BALANCED_TEXT =
        Pattern.compile("(?s)((?:\\\\."
            + "|[^\\\\{}]|[{](?:\\\\."
            + "|[^\\\\{}])*[}])*)\\}|.");

    /** "Matches input to the text formatter.  Always matches something
     *  in a non-empty string or input source.  After matching, one or
     *  more of the groups described by *_TOKEN declarations will
     *  be non-null.  See these declarations for descriptions of what
     *  this pattern matches.  To test whether .group(*_TOKEN) is null
     *  quickly, check for .end(*_TOKEN) > -1).  */
    private static final Pattern INPUT_PATTERN =
        Pattern.compile("(?s)(\\p{Blank}+)"
                        + "|(\\r?\\n((?:\\r?\\n)+)?)"
                        + "|\\\\([\\p{Blank}{}\\\\])"
                        + "|\\\\(\\p{Alpha}+)([{]?)"
                        + "|((?:[^\\p{Blank}\\r\\n\\\\{}]+))"
                        + "|(.)");

    /** Symbolic names for the groups in INPUT_PATTERN. */
    private static final int
        /** Blank or tab. */
        BLANK_TOKEN = 1,
        /** End of line or paragraph. */
        EOL_TOKEN = 2,
        /** End of paragraph (>1 newline). EOL_TOKEN group will also
         *  be present. */
        EOP_TOKEN = 3,
        /** \{, \}, \\, or \ .  .group(ESCAPED_CHAR_TOKEN) will be the
         *  character after the backslash. */
        ESCAPED_CHAR_TOKEN = 4,
        /** Command (\<alphabetic characters>).  .group(COMMAND_TOKEN)
         *  will be the characters after the backslash.  */
        COMMAND_TOKEN = 5,
        /** A '{' immediately following a command. When this group is present,
         *  .group(COMMAND_TOKEN) will also be present. */
        COMMAND_ARG_TOKEN = 6,
        /** Segment of other text (none of the above, not including
         *  any of the special characters \, {, or }). */
        TEXT_TOKEN = 7,
        /** A character that should not be here. */
        ERROR_TOKEN = 8;

    /** A new InputParser taking input from READER and sending tokens to
     *  OUT. */
    InputParser(Reader reader, Controller out) {
        _inp = new Scanner(reader);
        _out = out;
    }

    /** A new InputParser whose input is TEXT and that sends tokens to
     *  OUT. */
    InputParser(String text, Controller out) {
        _inp = new Scanner(text);
        _out = out;
    }

    /** Break all input source text into tokens, and send them to our
     *  output controller.  Finishes by calling .close on the controller.
     */
    void process() {
        while (true) {
            if (_inp.findWithinHorizon(INPUT_PATTERN, 0) != null) {
                MatchResult match = _inp.match();
                if (match.end(BLANK_TOKEN) > -1) {
                    _out.endWord();
                } else if (match.end(EOL_TOKEN) > -1) {
                    if (match.end(EOP_TOKEN) > -1) {
                        _out.endParagraph();
                    } else {
                        _out.addNewline(true);
                    }
                } else if (match.end(ESCAPED_CHAR_TOKEN) > -1) {
                    _out.addText(match.group(ESCAPED_CHAR_TOKEN));
                } else if (match.end(COMMAND_TOKEN) > -1) {
                    String comm = match.group(COMMAND_TOKEN);
                    if (!match.group(COMMAND_ARG_TOKEN).equals("")) {
                        if (_inp.findWithinHorizon(BALANCED_TEXT, 0) != null) {
                            MatchResult argmatch = _inp.match();
                            if (argmatch.group(1) != null) {
                                processCommand(comm, argmatch.group(1));
                            }
                        } else {
                            reportError("Unbalanced Text Error: %s",
                                match.group(COMMAND_ARG_TOKEN));
                        }
                    } else {
                        processCommand(comm, match.group(COMMAND_ARG_TOKEN));
                    }
                } else if (match.end(TEXT_TOKEN) > -1) {
                    _out.addText(match.group(TEXT_TOKEN));

                } else if (match.end(ERROR_TOKEN) > -1) {
                    FormatException.error(match.group(ERROR_TOKEN));
                }
            } else if (!_inp.hasNextLine()) {
                break;
            }
        }
        _out.close();
        _inp.close();
    }

    /** @return BOOLEAN to test if its a balanced TEXT.*/
    boolean balancedTest(String text) {
        Scanner input = new Scanner(text);
        if (input.findInLine(BALANCED_TEXT) != null) {
            MatchResult match = input.match();
            if (match.group(1) == null) {
                throw new FormatException("Unbalanced Text");
            } else {
                return true;
            }
        } else {
            throw new FormatException("Null Command Args");
        }
    }
    /** true if the string ARG is an integer.*/
    void checkNumeric(String arg) {
        Scanner input = new Scanner(arg);
        String digits = "([\\d]+)";
        if (input.findWithinHorizon(digits, 0) == null) {
            reportError("Non numeric argument ERROR: %s", arg);
            System.exit(1);
        }
        String spaces = "([\\s*]).+";
        Scanner inp = new Scanner(arg);
        if (inp.findWithinHorizon(spaces, 0) != null) {
            reportError("Spaces in argument Error: %s", arg);
            System.exit(1);
        }
        String multipleZeroes = "(^0[\\d+])|(^0[.+])";
        Scanner in = new Scanner(arg);
        if (in.findWithinHorizon(multipleZeroes, 0) != null) {
            reportError("zeroes preceding arg Error: %s", arg);
            System.exit(1);
        }
    }

    /** Process \COMMAND{ARG} or (if ARG is null) \COMMAND.  Call the
     *  appropriate methods in our Controller (_out). */
    private void processCommand(String command, String arg) {
        try {
            switch (command) {
            case "indent":
                checkNumeric(arg);
                _out.setIndentation(Integer.parseInt(arg));
                break;
            case "parindent":
                checkNumeric(arg);
                _out.setParIndentation(Integer.parseInt(arg));
                break;
            case "parskip":
                checkNumeric(arg);
                _out.setParSkip(Integer.parseInt(arg));
                break;
            case "textwidth":
                checkNumeric(arg);
                _out.setTextWidth(Integer.parseInt(arg));
                break;
            case "textheight":
                checkNumeric(arg);
                _out.setTextHeight(Integer.parseInt(arg));
                break;
            case "nofill":
                _out.setFill(false);
                break;
            case "fill":
                _out.setFill(true);
                break;
            case "justify":
                _out.setJustify(true);
                break;
            case "nojustify":
                _out.setJustify(false);
                break;
            case "endnote":
                _out.setEndnoteMode();
                InputParser subParser = new InputParser(arg, _out);
                subParser.process();
                _out.setNormalMode();
                break;
            default:
                reportError("unknown command: %s", command);
                break;
            }
        } catch (FormatException e) {
            reportError("ERROR: %s", e.getMessage());
        }
    }

    /** My input source. */
    private final Scanner _inp;
    /** The Controller to which I send input tokens. */
    private Controller _out;

}
