package tex61;

import java.util.ArrayList;

import static tex61.FormatException.reportError;

/** An object that receives a sequence of words of text and formats
 *  the words into filled and justified text lines that are sent to a receiver.
 *  @author Ian Fox
 */
class LineAssembler {

    /** A new, empty line assembler with default settings of all
     *  parameters, sending finished lines to PAGES. */
    LineAssembler(ParagraphCollector pages) {
        _pages = pages;
        _firstline = true;
        _lastline = false;
        _currL = new ArrayList<String>();
        _fill = true;
        _justify = true;
        _textWidth = Defaults.TEXT_WIDTH;
        _indent = Defaults.INDENTATION;
        _parindent = Defaults.PARAGRAPH_INDENTATION;
        _parSkip = Defaults.PARAGRAPH_SKIP;
        _tTextWid = _textWidth - dIndent().length();
        _veryfirstline = true;
    }
    /** resets _veryfirstline. */
    void veryfirstline() {
        _veryfirstline = true;
    }
    /** Add WRD to the formatted.
    text. */
    void addWord(String wrd) {
        if (_fill) {
            _tTextWid = _tTextWid - wrd.length() - 1;
            if ((_tTextWid == 0 || _tTextWid == -1) && _currL.size() > 0) {
                _currL.add(wrd);
                String line = formatLine();
                addLine(line);
                _tTextWid = _textWidth - dIndent().length();
            } else if (_tTextWid < -1 && _currL.size() > 0) {
                String line = formatLine();
                addLine(line);
                _tTextWid = _textWidth - wrd.length() - 1 - dIndent().length();
                _currL.add(wrd);
            } else {
                _currL.add(wrd);
            }
        } else {
            _currL.add(wrd);
        }
    }

    /** formats a line of text and returns it. */
    String formatLine() {
        String line;
        if (_firstline && !_veryfirstline) {
            for (int i = 0; i < _parSkip; i++) {
                _pages.addLine("", this);
            }
        }
        if (_justify && _fill) {
            String indent = dIndent();
            if (_lastline) {
                line = standardF(indent);
            } else {
                int characters = 0, wordCount = 0, i = 0;
                while (i < _currL.size()) {
                    characters += (_currL.get(i)).length();
                    wordCount++;
                    i++;
                }
                line = justify(indent, characters, wordCount);
            }
        } else {
            String indent = dIndent();
            line = standardF(indent);
        }
        return line;
    }

    /** Returns a STRING of a standard format INDENT.*/
    String standardF(String indent) {
        String line = indent + _currL.remove(0);
        while (_currL.size() > 0) {
            line += " " + _currL.remove(0);
        }
        return line;
    }
    /** Returns a string of dmined indention.*/
    String dIndent() {
        int indent;
        if (_firstline) {
            indent = _parindent + _indent;
        } else {
            indent = _indent;
        }
        String totalIndent = "";
        for (int count = 0; count != indent; count++) {
            totalIndent += " ";
        }
        return totalIndent;
    }
    /** returns a STRING justifited by the arguments INDENT.
    * CHARACTERS, COUNT. */
    String justify(String indent, int characters, int count) {
        String result = "";
        int b = _textWidth - indent.length() - characters;
        if (b >= (3 * (count - 1))) {
            for (int i = 0; i < _currL.size(); i++) {
                if (i == 0) {
                    result += indent + _currL.get(i);
                } else if ((i + 1) == _currL.size()) {
                    result += _currL.get(i);
                } else {
                    result += _currL.get(i) + "   ";
                }
            }
        } else {
            int totalSpaces = 0;
            for (int i = 0; i < _currL.size(); i++) {
                if (i == 0) {
                    result = indent + _currL.get(i);
                } else {
                    String blanks = spaceHelper(b, count, totalSpaces, i);
                    result += blanks + _currL.get(i);
                    totalSpaces += blanks.length();
                }
            }
        }
        return result;
    }
    /** @return STRING of the space from the params by B, N, TOTSPACES, I. */
    String spaceHelper(int b, int n, int totSpaces, int i) {
        int blanks = (int) (0.5 + ((double) b * (double) i) / (double) (n - 1));
        if (totSpaces != 0) {
            blanks = blanks - totSpaces;
        }
        String blankStr = "";
        for (int el = 0; el != blanks; el++) {
            blankStr += " ";
        }
        return blankStr;
    }
    /** returns an eol only called when its eol.*/
    void eol()  {
        if (!_fill && _currL.size() > 0) {
            String line = formatLine();
            addLine(line);
        }

    }
    /** Add LINE to our output, with no preceding paragraph skip.  There must
     *  not be an unfinished line pending. */
    void addLine(String line) {
        _firstline = false;
        _veryfirstline = false;
        _pages.addLine(line, this);
        _currL = new ArrayList<String>();

    }
    /** Set the current indentation to VAL. VAL >= 0. */
    void setIndentation(int val) {
        if (val > _textWidth || (val + _parindent) > _textWidth) {
            reportError("reater than textWidth error: %s", val);
        }
        _indent = val;
        _tTextWid = _textWidth - dIndent().length();
    }

    /** Set the current paragraph indentation to VAL. VAL >= 0. */
    void setParIndentation(int val) {
        if (val > _textWidth || (val + _indent) > _textWidth) {
            reportError("Par indent too big error: %s", val);
        } else {
            _parindent = val;
            _tTextWid = _textWidth - dIndent().length();
        }
    }

    /** Set the text width to VAL, where VAL >= 0. */
    void setTextWidth(int val) {
        int temp = _textWidth;
        _textWidth = val;
        if (!_lastline && !_veryfirstline && !_firstline) {
            _tTextWid = _textWidth - (temp + _tTextWid);
        } else {
            _tTextWid = _textWidth - dIndent().length();
        }
    }

    /** Iff SETTING, set fill mode. */
    void setFill(boolean setting) {
        _fill = setting;
    }

    /** Iff SETTING, set justify mode (which is active only when filling is
     *  also on). */
    void setJustify(boolean setting) {
        _justify = setting;
    }

    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParSkip(int val) {
        _parSkip = val;
    }


    /** If there is a current unfinished paragraph pending, close it
     *  out and start a new one. */
    void endParagraph() {
        if (_currL.size() > 0) {
            _lastline = true;
            String line = formatLine();
            _veryfirstline = false;
            _pages.addLine(line, this);
            _firstline = true;
            _tTextWid = _textWidth - dIndent().length();
            _lastline = false;
        }
    }
    /** @return INT the value of indent.*/
    int getIndent() {
        return _indent;
    }
    /** @returns INT of value of textwidht.*/
    int getWidth() {
        return _textWidth;
    }
    /** @returns STRING of the curr line.*/
    String getLine() {
        if (_currL.size() > 0) {
            return _currL.get(0);
        } else {
            return null;
        }
    }
    /** Destination given in constructor for formatted lines. */
    private final ParagraphCollector _pages;
    /** an array of current end notes. */
    private ArrayList<String> _currL;
    /** a boolean to indicate whether to fill. */
    private boolean _fill;
    /** a boolean to indicate whether to justify.*/
    private boolean _justify;
    /** a boolean to indicate whether to firstline.*/
    private boolean _firstline;
    /** a boolean to indicate whether to lastline. */
    private boolean _lastline;
    /** an int of textwidth .*/
    private int _textWidth;
    /** an int of temp textwidth .*/
    private int _tTextWid;
    /** an int of indent.*/
    private int _indent;
    /** an int of parindent.*/
    private int _parindent;
    /** an int of parskip.*/
    private int _parSkip;
    /** an int of textheight.*/
    private int _textHeight;
    /** the very firstline of endnotes.*/
    private boolean _veryfirstline;

}
