package tex61;
import java.io.PrintWriter;

/** A PageAssembler that sends lines immediately to a PrintWriter, with
 *  terminating newlines.
 *  @author Ian Fox
 */
class PagePrinter {

    /** A new PagePrinter that sends lines to PRINT. */
    PagePrinter(PrintWriter print) {
        _out = print;

    }
    /** Print LINE to my output. */
    void write(String line) {
        _out.println(line);

    }
    /** to where the page prints.*/
    private PrintWriter _out;
}
