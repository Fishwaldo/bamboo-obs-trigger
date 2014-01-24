package ac.dynam.bamboo.plugins.obstrigger.manager;

import ac.dynam.bamboo.plugins.obstrigger.configuration.AbstractOBSConfigurator;
import ac.dynam.bamboo.plugins.obstrigger.manager.EasySSLProtocolSocketFactory;
import ac.dynam.bamboo.plugins.obstrigger.manager.OBSResult;

import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.InputSource;


import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class OBSApplicationManagerImpl implements OBSApplicationManager
{
    private static final Logger log = Logger.getLogger(OBSApplicationManagerImpl.class);

    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties

    private final String obsapiUrl;
    private final HttpClient client;
    @NotNull
    private final CustomVariableContext customVariableContext;

    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors

    public OBSApplicationManagerImpl(@NotNull OBSConnection tomcatCredentials, @NotNull CommonTaskContext taskContext, @NotNull final CustomVariableContext customVariableContext) throws TaskException
    {
        this.customVariableContext = customVariableContext;
        try
        {
            this.obsapiUrl = new URL(customVariableContext.substituteString(tomcatCredentials.getURL())).toString();
        }
        catch (MalformedURLException e)
        {
            throw new TaskException("Malformed OBS API URL, please fix your OBS Deploy Task configuration.", e);
        }
        HttpClientParams httpClientParams = new HttpClientParams();
        httpClientParams.setAuthenticationPreemptive(true);
        Protocol easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", easyhttps);
        client = new HttpClient(httpClientParams);
        client.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(customVariableContext.substituteString(tomcatCredentials.getUsername()), customVariableContext.substituteString(tomcatCredentials.getPassword())));
    }


    @NotNull
    @Override
    public OBSResult deployApplication(@NotNull final String pkg, @NotNull final String prj) throws IOException
    {

        try
        {
            final String url = new StringBuilder(obsapiUrl)
                    .append("/source/")
                    .append(encode(customVariableContext.substituteString(prj)))
                    .append("/")
                    .append(encode(customVariableContext.substituteString(pkg)))
                    .append("?cmd=runservice")
                    .toString();

            final PostMethod postMethod = new PostMethod(url);
            //putMethod.setRequestEntity(new InputStreamRequestEntity(inputStream, file.length()));
            final String result = execute(postMethod);
            return OBSResult.parse(result);
        }
        finally {
            //IOUtils.closeQuietly(inputStream);
        }
    }

    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators

    @NotNull
    private String execute(HttpMethod httpMethod) throws IOException
    {
        try
        {
        	log.warn("Starting");
            httpMethod.setDoAuthentication(true);
            httpMethod.addRequestHeader("User-Agent", "Atlassian OBS API");
            httpMethod.getHostAuthState().isPreemptive();
            client.executeMethod(httpMethod);

            final int status = httpMethod.getStatusCode();
            if (status >= HttpServletResponse.SC_OK && status < HttpServletResponse.SC_MULTIPLE_CHOICES)
            {
                return httpMethod.getResponseBodyAsString();
            }
            else
            {
                if (status == HttpServletResponse.SC_FORBIDDEN || status == HttpServletResponse.SC_UNAUTHORIZED)
                {
                    throw new IOException("Could not connect to OBS API at '" + httpMethod.getURI() + "' because the username and password provided is not authorized.");
                }
//            	try {
        	    	XPathFactory xpathFactory = XPathFactory.newInstance();
        	    	XPath xpath = xpathFactory.newXPath();
        	
        	    	InputSource source = new InputSource(new StringReader(httpMethod.getResponseBodyAsString()));
        	    	log.warn("Got a Weird Return:" + httpMethod.getResponseBodyAsString());
        	    	String statuscode;
        	    	try {
        	    		statuscode = xpath.evaluate("/status/@code", source);
        	    	} catch (Exception ex) {
        	    		throw new IOException("Can't Parse Response Code");
        	    	}
        	    	log.error("Status:" + statuscode);
        	    	if (statuscode.startsWith("not_found"))
        	        {
            	    	throw new IOException("OBS Reported a Error:" + httpMethod.getResponseBodyAsString());
        	        }
        	        else
        	        {
                        throw new IOException("Could not connect to OBS API at '" + httpMethod.getURI() + "'. Response code: " + status);
        	        }
  //          	} catch (Exception ex) {
  //          		throw ex;
  //          	}
            }
        }
        finally
        {
            httpMethod.releaseConnection();
        }
    }

    private static String encode(String value)
    {
        try
        {
            return URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalStateException(e);
        }
    }

}
