package ac.dynam.bamboo.plugins.obstrigger.manager;

import ac.dynam.bamboo.plugins.obstrigger.configuration.AbstractTomcatConfigurator;
import ac.dynam.bamboo.plugins.obstrigger.manager.EasySSLProtocolSocketFactory;

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
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class TomcatApplicationManagerImpl implements TomcatApplicationManager
{
    private static final Logger log = Logger.getLogger(TomcatApplicationManagerImpl.class);

    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties

    private final String obsapiUrl;
    private final HttpClient client;
    @NotNull
    private final CustomVariableContext customVariableContext;

    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors

    public TomcatApplicationManagerImpl(@NotNull TomcatConnection tomcatCredentials, @NotNull CommonTaskContext taskContext, @NotNull final CustomVariableContext customVariableContext) throws TaskException
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
    public TomcatResult deployApplication(@NotNull final String pkg, @NotNull final String prj) throws IOException
    {

        try
        {
            final String url = new StringBuilder(obsapiUrl)
                    .append("/package/execute_services?package=")
                    .append(encode(customVariableContext.substituteString(pkg)))
                    .append("&project=")
                    .append(encode(customVariableContext.substituteString(prj))).toString();

            final PutMethod putMethod = new PutMethod(url);
            //putMethod.setRequestEntity(new InputStreamRequestEntity(inputStream, file.length()));
            final String result = execute(putMethod);
            return TomcatResult.parse(result);
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
                throw new IOException("Could not connect to OBS API at '" + httpMethod.getURI() + "'. Response code: " + status);
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
