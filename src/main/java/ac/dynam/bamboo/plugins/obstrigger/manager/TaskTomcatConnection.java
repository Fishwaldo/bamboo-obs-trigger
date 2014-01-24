package ac.dynam.bamboo.plugins.obstrigger.manager;

import ac.dynam.bamboo.plugins.obstrigger.configuration.DeployAppConfigurator;
import com.atlassian.bamboo.security.EncryptionService;
import com.atlassian.bamboo.task.CommonTaskContext;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class TaskTomcatConnection implements TomcatConnection
{
    private static final Logger log = Logger.getLogger(TaskTomcatConnection.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties

    private final String URL;
    private final String username;
    private final String password;

    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors

    public TaskTomcatConnection(@NotNull CommonTaskContext taskContext, final EncryptionService encryptionService)
    {
        this.URL = taskContext.getConfigurationMap().get(DeployAppConfigurator.OBS_URL);
        this.username = taskContext.getConfigurationMap().get(DeployAppConfigurator.OBS_USERNAME);
        this.password = encryptionService.decrypt(taskContext.getConfigurationMap().get(DeployAppConfigurator.OBS_PASSWORD));
    }

    // ----------------------------------------------------------------------------------------------- Interface Methods
    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public String getURL()
    {
        return URL;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public String getPassword()
    {
        return password;
    }


    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
