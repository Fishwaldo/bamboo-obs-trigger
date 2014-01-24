package ac.dynam.bamboo.plugins.obstrigger.manager;

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.InputSource;

public final class OBSResult
{
    //private static final java.util.logging.Logger log = Logger.getLogger(TomcatResult.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties

    private final boolean successful;
    private final String reason;

    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors

    public OBSResult(final boolean successful, final String reason)
    {
        this.successful = successful;
        this.reason = reason;
    }

    // ----------------------------------------------------------------------------------------------- Interface Methods
    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods

    public boolean isSuccessful()
    {
        return successful;
    }

    public String getReason()
    {
        return reason;
    }

    @NotNull
    public static OBSResult parse(@NotNull String line)
    {
    	
    	try {
	    	XPathFactory xpathFactory = XPathFactory.newInstance();
	    	XPath xpath = xpathFactory.newXPath();
	
	    	InputSource source = new InputSource(new StringReader(
	    	    line));
	    	String status = xpath.evaluate("/status/@code", source);
	
	    	//log.warning("satus=" + status);
	    	
	    	
	    	
	    	if (status.startsWith("ok"))
	        {
	            return new OBSResult(true, line);
	        }
	        else
	        {
	            return new OBSResult(false, line);
	        }
    	} catch (Exception ex) {
    		return new OBSResult(false, ex.getMessage());
    	}
    }

    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
