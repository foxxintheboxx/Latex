package tex61;
import java.util.ArrayList;
import java.io.PrintWriter;

import static tex61.FormatException.reportError;

/** Receives (partial) words and commands, performs commands, and
 *  accumulates and formats words into lines of text, which are sent to a
 *  designated PageAssembler.  At any given time, a Controller has a
 *  current word, which may be added to by addText, a current list of
 *  words that are being accumulated into a line of text, and a list of
 *  lines of endnotes.
 *  @author Ian Fox
 */
class Controller {

    /** A new Controller that sends formatted output to OUT. */
    Controller(PrintWriter out) {
        _out = out;
        ArrayList<String> lineStorage = new ArrayList<String>();
        _printer = new PagePrinter(_out);
        _pages = new ParagraphCollector(_printer);
        _line = new LineAssembler(_pages);
        _currWord = "";
        _refNum = 0;
        _endnoteMode = false;
        _currEndWord = "";
        _endnotes = new EndNoteAssembler(_pages);

    }

    /** Add TEXT to the end of the word of formatted text currently
     *  being accumulated. */
    void addText(String text) {
        if (!_endnoteMode) {
            _currWord += text;
        } else {
            _currEndWord += text;
        }
    }
    /** Finish any current word of text and, if present, add to the
     *  list of words for the next line.  Has no effect if no unfinished
     *  word is being accumulated. */
    void endWord() {
        if (!_endnoteMode) {
            if (!_currWord.equals("")) {
                _line.addWord(_currWord);
                _currWord = "";
            }
        } else {
            if (!_currEndWord.equals("")) {
                _endnotes.addWord(_currEndWord);
                _currEndWord = "";
            }
        }
    }

    /** Finish any current word of formatted text and process an EOL.
     *  according to the current formatting parameters. */
    void addNewline(boolean eol) {
        endWord();
        if (eol) {
            if (_endnoteMode) {
                _endnotes.eol();
            } else {
                _line.eol();
            }
        }
    }
    /** Finish any current word of formatted text, format and output any
     *  current line of text, and start a new paragraph. */
    void endParagraph() {
        endWord();
        if (_endnoteMode) {
            _endnotes.endParagraph();
        } else {
            _line.endParagraph();
        }
    }


    /** Set the current text height (number of lines per page) to VAL, if
     *  it is a valid setting.  Ignored when accumulating an endnote. */
    void setTextHeight(int val)  {
        if (val > 0) {
            if (!_endnoteMode) {
                _pages.setTextHeight(val);
            }
        } else {
            reportError("Error TextHeight: %d must be greater than 0", val);
        }
    }

    /** Set the current text width (width of lines including indentation)
     *  to VAL, if it is a valid setting. */
    void setTextWidth(int val)   {
        if (val > 0) {
            if (_endnoteMode) {
                _endnotes.setTextWidth(val);
            } else {
                _line.setTextWidth(val);
            }
        } else {
            reportError("Error Negative or No Text Width:%d", val);
        }
    }

    /** Set the current text indentation (number of spaces inserted before
     *  each line of formatted text) to VAL, if it is a valid setting. */
    void setIndentation(int val)  {
        if (val >= 0) {
            if (_endnoteMode) {
                _endnotes.setIndentation(val);
            } else {
                _line.setIndentation(val);
            }
        } else {
            reportError("Error Negative Indentation: %d", val);
        }
    }

    /** Set the current paragraph indentation (number of spaces inserted before
     *  first line of a paragraph in addition to indentation) to VAL, if it is
     *  a valid setting. */
    void setParIndentation(int val) {
        if (_endnoteMode) {
            if (_endnotes.getIndent() + val >= 0) {
                _endnotes.setParIndentation(val);
            } else {
                reportError("ErrorNegative Paragraph Indentation: %d", val);
            }
        } else {
            if (_line.getIndent() + val >= 0) {
                _line.setParIndentation(val);
            } else {
                reportError("ErrorNegative Paragraph Indentation: %d", val);
            }
        }
    }

    /** Set the current paragraph skip (number of blank lines inserted before
     *  a new paragraph, if it is not the first on a page) to VAL, if it is
     *  a valid setting. */
    void setParSkip(int val) {
        if (val >= 0) {
            _line.setParSkip(val);
        } else {
            reportError("Error Negative Paragraph Skip: %d", val);
        }
    }

    /** Iff ON, begin filling lines of formatted text. */
    void setFill(boolean on) {
        _line.setFill(on);
    }

    /** Iff ON, begin justifying lines of formatted text whenever filling is
     *  also on. */
    void setJustify(boolean on) {
        _line.setJustify(on);
    }

    /** Finish the current formatted document or endnote (depending on mode).
     *  Formats and outputs all pending text. */
    void close() {
        endParagraph();
        if (!_endnoteMode) {
            _endnotes.writeEndnotes();
        }
    }
    /** returns a BOOLEAN of the mode.*/
    boolean getMode() {
        return _endnoteMode;
    }
    /** Start directing all formatted text to the endnote assembler. */
    void setEndnoteMode()  {
        if (_endnoteMode) {
            reportError("Nested EndNote Error");
        }
        _refNum += 1;
        String count = "[" + Integer.toString(_refNum) + "]";
        addText(count);
        _endnoteMode = true;
        _endnotes.setFirstLine();
        addText(count + " ");
    }

    /** Return to directing all formatted text to _mainText. */
    void setNormalMode() {
        _endnoteMode = false;
    }
    /** @returns String of the current word. */
    String getCurrWord() {
        return _currWord;
    }
    /** @returns String of the current word. */
    String getCurrEndWord() {
        return _currEndWord;
    }

    /** True iff we are currently processing an endnote. */
    private PrintWriter _out;
    /** the boolean if in endnote mode. */
    private boolean _endnoteMode;
    /** Number of next endnote. */
    private int _refNum;
    /** the pages that will be printed out.*/
    private ParagraphCollector _pages;
    /** the line assembler. */
    private LineAssembler _line;
    /** stored text, an array of words. */
    private String _currWord;
    /** stored end word.*/
    private String _currEndWord;
    /** the used printer. */
    private PagePrinter _printer;
    /** the endnotes using an endnote assembler.*/
    private EndNoteAssembler _endnotes;
}

