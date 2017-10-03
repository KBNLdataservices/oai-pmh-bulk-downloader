package nl.kb.xslt;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XsltTransformerFactory {

    public PipedXsltTransformer newPipedXsltTransformer(InputStream... xslts)
            throws IOException, TransformerConfigurationException {

        final List<StreamSource> sources = new ArrayList<>();
        for (InputStream xslt : xslts) {
            sources.add(new StreamSource(xslt));
        }
        return PipedXsltTransformer.newInstance((StreamSource[]) sources.toArray());
    }
}
