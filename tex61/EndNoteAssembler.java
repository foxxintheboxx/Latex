package tex61;
import java.util.ArrayList;

/** An object that receives a sequence of words of text and formats
 *  the words into filled and justified text lines that are sent to a receiver.
 *  @author Ian Fox
 */
class EndNoteAssembler extends LineAssembler {
    /** creates a new EndNoteAssembler with PAGES.*/
    EndNoteAssembler(ParagraphCollector pages) {
        super(pages);
        _pages = pages;
        _justify = true;
        _fill = true;
        _lastline = false;
        _currL = new ArrayList<String>();
        _indent = Defaults.ENDNOTE_INDENTATION;
        _parindent = Defaults.ENDNOTE_PARAGRAPH_INDENTATION;
        _textWidth = Defaults.ENDNOTE_TEXT_WIDTH;
        _tTextWid = _textWidth;
        _parSkip = Defaults.ENDNOTE_PARAGRAPH_SKIP;
        _finishedLines = new ArrayList<String>();
        _veryfirstline = true;
    }
    @Override
    /** adds a word WRD which is a string.*/
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
    @Override
    /** adds a new line LINE.*/
    void addLine(String line) {
        _firstline = false;
        _veryfirstline = false;
        _finishedLines.add(line);
        _currL = new ArrayList<String>();

    }
    @Override
    /** ends the paragraphs. */
    void endParagraph() {
        if (_currL.size() > 0) {
            _lastline = true;
            String line = formatLine();
            _veryfirstline = false;
            _finishedLines.add(line);
            _tTextWid = _textWidth;
            _firstline = true;
            _lastline = false;
        }
    }
    @Override
    /** returns the STRING of formatline().*/
    String formatLine() {
        String line;
        if (_firstline && !_veryfirstline) {
            for (int i = 0; i < _parSkip; i++) {
                _finishedLines.add("");
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
    @Override
    /** STRING of dmined indentations.*/
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

    /** Outputs the collected endnotes at the end of the text.*/
    void writeEndnotes() {
        if (_finishedLines.size() > 0) {
            _pages.setTextHeight(_pages.getTextHeight());
            for (int i = 0; i < _finishedLines.size(); i++) {
                String line = _finishedLines.get(i);
                _pages.addLine(line, this);
            }
        }
    }
    @Override
    /** returns a STRING justifited by the arguments INDENT.
    * CHARACTERS, COUNT. */
    String justify(String indent, int characters, int count) {
        String result = "";
        int b = _textWidth - indent.length() - characters;
        if (b >= (3 * (count - 1))) {
            for (int i = 0; i < _currL.size(); i++) {
                if (i == 0) {
                    result += indent + _currL.get(i);
                } else if (_currL.size() == 2) {
                    result += "   " + _currL.get(i);
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
    /** sets the very first line to true.*/
    void setFirstLine() {
        _firstline = true;
    }
    @Override
    /** STRING of the spaces from the parameters by B, N, TOTSPACES, I. */
    String spaceHelper(int b, int n, int totSpaces, int i) {
        int blanks = (int) (0.5 + ((double) (b * i) / (double) (n - 1)));
        if (totSpaces != 0) {
            blanks = blanks - totSpaces;
        }
        String blankStr = "";
        for (int el = 0; el != blanks; el++) {
            blankStr += " ";
        }
        return blankStr;
    }
    @Override
    String standardF(String indent) {
        String line = indent + _currL.remove(0);
        while (_currL.size() > 0) {
            line += " " + _currL.remove(0);
        }
        return line;
    }
    @Override
    void eol()  {
        if (!_fill && _currL.size() > 0) {
            String line = formatLine();
            addLine(line);
        }

    }
    @Override
    /** resets _veryfirstline. */
    void veryfirstline() {
        _veryfirstline = true;
    }
    @Override
    /** Set the current indentation to VAL. VAL >= 0. */
    void setIndentation(int val) {
        if (val > _textWidth || (val + _parindent) > _textWidth) {
            throw new FormatException("indentation greater than textWidth");
        }
        _indent = val;
        _tTextWid = _textWidth - dIndent().length();
    }
    @Override
    /** Set the current paragraph indentation to VAL. VAL >= 0. */
    void setParIndentation(int val) {
        if (val > _textWidth || (val + _indent) > _textWidth) {
            throw new FormatException("Par indent can't be bigger than indent");
        } else {
            _parindent = val;
            _tTextWid = _textWidth - dIndent().length();
        }
    }
    @Override
    /** Set the text width to VAL, where VAL >= 0. */
    void setTextWidth(int val) {
        int temp = _textWidth;
        _textWidth = val;
        if (!_lastline) {
            if (temp > _textWidth) {
                if (temp == _tTextWid) {
                    _tTextWid = _textWidth;
                } else if ((_textWidth - (temp - _tTextWid)) >= _textWidth) {
                    addLine(formatLine());
                    _tTextWid = _textWidth;
                } else {
                    _tTextWid = _textWidth - (temp - _tTextWid);
                }
            } else {
                _tTextWid = _textWidth - (temp + _tTextWid);
            }
        } else {
            _tTextWid = _textWidth - dIndent().length();
        }
    }
    @Override
    /** Iff ON, set fill mode. */
    void setFill(boolean setting) {
        _fill = setting;
    }
    @Override
    /** Iff ON, set justify mode (which is active only when filling is
     *  also on). */
    void setJustify(boolean setting) {
        _justify = setting;
    }
    @Override
    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParSkip(int val) {
        _parSkip = val;
    }
    @Override
    /** @return INT the value of indent.*/
    int getIndent() {
        return _indent;
    }
    /** all the formatted line collected in _pages.*/
    private ParagraphCollector _pages;
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
    /** an array of finished lines.*/
    private ArrayList<String> _finishedLines;
}
