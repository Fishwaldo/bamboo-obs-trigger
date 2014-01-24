package ac.dynam.bamboo.plugins.obstrigger.configuration;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.security.EncryptionService;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.opensymphony.xwork.TextProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public abstract class AbstractOBSConfigurator extends AbstractTaskConfigurator
{
    private static final Logger log = Logger.getLogger(AbstractOBSConfigurator.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    public static final String OBS_URL = "obsUrl";
    public static final String OBS_USERNAME = "obsUsername";
    public static final String OBS_PLAIN_PASSWORD = "obsPassword";
    public static final String OBS_PASSWORD = "encObsPassword";


    public static final String OBS_PACKAGE = "obspackage";
    public static final String OBS_PROJECT = "obsproject";

    private static final Set<String> FIELDS_TO_COPY = ImmutableSet.<String>builder().add(OBS_URL,
                                                                                         OBS_USERNAME,
                                                                                         OBS_PACKAGE,
                                                                                         OBS_PROJECT,
                                                                                         OBS_PLAIN_PASSWORD,OBS_PASSWORD).build();
    private static final String PASSWORD_CHANGE = "passwordChange";

    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies

    protected TextProvider textProvider;
    private EncryptionService encryptionService;

    // ---------------------------------------------------------------------------------------------------- Constructors
    // ----------------------------------------------------------------------------------------------- Interface Methods

        @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);
        context.put(OBS_URL, "https://api.opensuse.org/");
        //context.put(OBS_USERNAME, "admin");
        //context.put(APP_CONTEXT, "/test");
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        taskConfiguratorHelper.populateContextWithConfiguration(context, taskDefinition, Iterables.concat(FIELDS_TO_COPY, getFieldsToCopy()));
    }

    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        taskConfiguratorHelper.populateContextWithConfiguration(context, taskDefinition, Iterables.concat(FIELDS_TO_COPY, getFieldsToCopy()));
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        final String url = params.getString(OBS_URL);
        if (StringUtils.isEmpty(url))
        {
            errorCollection.addError(OBS_URL, textProvider.getText("obs.url.error"));
        }

        if (StringUtils.isEmpty(params.getString(OBS_USERNAME)))
        {
            errorCollection.addError(OBS_USERNAME, textProvider.getText("obs.username.error"));
        }

        if (StringUtils.isEmpty(params.getString(OBS_PLAIN_PASSWORD)) && params.getBoolean(PASSWORD_CHANGE))
        {
            errorCollection.addError(OBS_PLAIN_PASSWORD, textProvider.getText("obs.password.error"));
        }

        if (StringUtils.isEmpty(params.getString(OBS_PACKAGE)))
        {
            errorCollection.addError(OBS_PACKAGE, textProvider.getText("obs.package.error"));
        }
        if (StringUtils.isEmpty(params.getString(OBS_PROJECT)))
        {
            errorCollection.addError(OBS_PROJECT, textProvider.getText("obs.project.error"));
        }

    }

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

        taskConfiguratorHelper.populateTaskConfigMapWithActionParameters(config, params, Iterables.concat(FIELDS_TO_COPY, getFieldsToCopy()));

        String passwordChange = params.getString(PASSWORD_CHANGE);
        if ("true".equals(passwordChange))
        {
            final String password = params.getString(OBS_PLAIN_PASSWORD);
            config.put(OBS_PASSWORD, encryptionService.encrypt(password));
        }
        else if (previousTaskDefinition != null)
        {
            config.put(OBS_PASSWORD, previousTaskDefinition.getConfiguration().get(OBS_PASSWORD));
        }
        else
        {
            final String password = params.getString(OBS_PLAIN_PASSWORD);
            config.put(OBS_PASSWORD, encryptionService.encrypt(password));
        }

        return config;
    }

    protected abstract Set<String> getFieldsToCopy();

    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators

    public void setTextProvider(final TextProvider textProvider)
    {
        this.textProvider = textProvider;
    }

    public void setEncryptionService(final EncryptionService encryptionService)
    {
        this.encryptionService = encryptionService;
    }
}
