package ac.dynam.bamboo.plugins.obstrigger.tasks;

import com.atlassian.bamboo.build.logger.BuildLogger;
import ac.dynam.bamboo.plugins.obstrigger.configuration.DeployAppConfigurator;
import ac.dynam.bamboo.plugins.obstrigger.manager.TaskTomcatConnection;
import ac.dynam.bamboo.plugins.obstrigger.manager.TomcatApplicationManager;
import ac.dynam.bamboo.plugins.obstrigger.manager.TomcatApplicationManagerImpl;
import ac.dynam.bamboo.plugins.obstrigger.manager.TomcatConnection;
import ac.dynam.bamboo.plugins.obstrigger.manager.TomcatResult;
import com.atlassian.bamboo.security.EncryptionService;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.CommonTaskType;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.variable.CustomVariableContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Deploys a war on the local file system to Tomcat. Will stop
 */
public class TriggerBuild implements CommonTaskType
{
    private static final Logger log = Logger.getLogger(TriggerBuild.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    private final EncryptionService encryptionService;
    private final CustomVariableContext customVariableContext;

    // ---------------------------------------------------------------------------------------------------- Constructors
    public TriggerBuild(final EncryptionService encryptionService, final CustomVariableContext customVariableContext)
    {
        this.encryptionService = encryptionService;
        this.customVariableContext = customVariableContext;
    }
    // ----------------------------------------------------------------------------------------------- Interface Methods

    @NotNull
    @java.lang.Override
    public TaskResult execute(@NotNull final CommonTaskContext taskContext) throws TaskException
    {
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        final TomcatConnection connection = new TaskTomcatConnection(taskContext, encryptionService);
        final TomcatApplicationManager tomcatManager = new TomcatApplicationManagerImpl(connection, taskContext, customVariableContext);
        final TaskResultBuilder taskResultBuilder = TaskResultBuilder.newBuilder(taskContext);

        final String prj = taskContext.getConfigurationMap().get(DeployAppConfigurator.OBS_PROJECT);
        final String pkg = taskContext.getConfigurationMap().get(DeployAppConfigurator.OBS_PACKAGE);

        try
        {
            final StringBuilder deployMessage = new StringBuilder()
                    .append("Triggering Service Run on Project '")
                    .append(prj)
                    .append("' in Package '")
                    .append(pkg)
                    .append("' to server '")
                    .append(connection.getURL())
                    .append("'");

            buildLogger.addBuildLogEntry(deployMessage.toString());

            final TomcatResult result = tomcatManager.deployApplication(pkg, prj);
            if (result.isSuccessful())
            {
                buildLogger.addBuildLogEntry("Application was successfully deployed.");
                taskResultBuilder.success();
            }
            else
            {
                final String message = "Application failed to deploy: " + result.getReason();
                buildLogger.addErrorLogEntry(message);
                taskResultBuilder.failed();
            }
        }
        catch (IOException e)
        {
            final String log = "Could not deploy application: " + e.getMessage();
            buildLogger.addErrorLogEntry(log);
            taskResultBuilder.failedWithError();
        }

        return taskResultBuilder.build();
    }

    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
