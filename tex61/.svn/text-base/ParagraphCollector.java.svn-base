package tex61;


/** A PageAssembler that collects its lines into a designated List.
 *  @author Ian Fox
 */
class ParagraphCollector {

    /** A new PageCollector that stores lines in OUT and PRINT. */
    ParagraphCollector(PagePrinter print) {
        _out = print;
        _textHeight = Defaults.TEXT_HEIGHT;
        _tempTextHeight = _textHeight;
        _endtextHeight = _tempEndHeight = _textHeight;
    }
    /** Add LINE to my List. */
    void write(String line) {
        _out.write(line);
    }
    /** adds a LINE to the outputof L. */
    void addLine(String line, LineAssembler l) {
        _tempTextHeight -= 1;
        if (_tempTextHeight < 0) {
            l.veryfirstline();
            if (!line.equals("")) {
                write("\f" + line);
                _tempTextHeight = _textHeight - 1;
            }
        } else {
            write(line);
        }
    }
    /** sets the currents text to VAL. */
    void setTextHeight(int val) {
        if (_tempTextHeight == _textHeight) {
            _tempTextHeight = _textHeight = val;
        }
        _textHeight = val;
    }
    /** returns the temp text INT height.*/
    int getTempTextHeight() {
        return _tempTextHeight;
    }
    /** returns an INT of the getTextHeight. */
    int getTextHeight() {
        return _textHeight;
    }
    /** the curr amount of lines in a page. */
    private int _tempTextHeight;
    /** the total amount of lines in a page.*/
    private int _textHeight;
    /** the curr amount of lines in an endnote. */
    private int _tempEndHeight;
    /** the total amount of line in an endnote. */
    private int _endtextHeight;
    /** the printer that outputs .*/
    private PagePrinter _out;
}
