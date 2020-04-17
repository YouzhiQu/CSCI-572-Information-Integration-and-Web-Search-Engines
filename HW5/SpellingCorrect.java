
package spellingCorrect;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
public class spellingCorrect {
    public static void main(String args[]) throws Exception
    {
        PrintWriter writer = new PrintWriter ("/home/youzhi/Desktop/cs572/big.txt");
        String dirPath = "/home/youzhi/Desktop/cs572/guardiannews";
        File dir = new File(dirPath);
        int count = 1;

        try{
            for(File file: dir.listFiles())
            {
                count++;
                BodyContentHandler handler = new BodyContentHandler(-1);
                Metadata metadata = new Metadata();
                ParseContext pcontext = new ParseContext();
                HtmlParser htmlparser = new HtmlParser();
                FileInputStream inputstream = new FileInputStream(file);
                htmlparser.parse(inputstream, handler, metadata,pcontext);
                String content = handler.toString();
                String words[] = content.split(" ");
                for(String t: words)
                {
                    if(t.matches("[a-zA-Z]+\\.?"))
                    {
                        writer.print(t + "\n");
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Caught IOException: " + e.getMessage());
            e.printStackTrace();
        }
        writer.close();
        System.out.println(count);
    }
}
