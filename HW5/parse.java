package spellingCorrect;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
public class parse {
    public static void main(String args[]) throws Exception {
        String op = "/home/youzhi/Desktop/cs572/guardiannews/";
        String dirPath = "/home/youzhi/Desktop/cs572/parsedData/";
        File dir = new File(dirPath);

        int count = 1;

        try {

            for (File file : dir.listFiles()) {

                PrintWriter writer = new PrintWriter(op + file.getName().substring(0,file.getName().length()-5));
                count++;

                BodyContentHandler handler = new BodyContentHandler(-1);

                Metadata metadata = new Metadata();

                ParseContext pcontext = new ParseContext();

                HtmlParser htmlparser = new HtmlParser();

                FileInputStream inputstream = new FileInputStream(file);

                htmlparser.parse(inputstream, handler, metadata, pcontext);

                String content = handler.toString().trim().replaceAll(" +", " ").replaceAll("[\r\n]+", "\n");
                writer.print(content);
                writer.close();
            }

        } catch (Exception e) {

            System.err.println("Caught IOException: " + e.getMessage());

            e.printStackTrace();

        }

    }

}
