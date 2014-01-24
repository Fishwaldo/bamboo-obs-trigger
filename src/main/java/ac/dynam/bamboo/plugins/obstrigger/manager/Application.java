package ac.dynam.bamboo.plugins.obstrigger.manager;

import org.apache.log4j.Logger;

public final class Application
{
    private static final Logger log = Logger.getLogger(Application.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties

    private final String context;
    private final ApplicationState applicationState;
    private final int activeSessions;
    private final String name;

    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors

    public Application(final String context, final String applicationState, final String activeSessions, final String name)
    {
        this.context = context;
        this.applicationState = ApplicationState.valueOf(applicationState.toUpperCase());
        this.activeSessions = Integer.parseInt(activeSessions);
        this.name = name;
    }

    // ----------------------------------------------------------------------------------------------- Interface Methods
    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods

    public String getContext()
    {
        return context;
    }

    public ApplicationState getApplicationState()
    {
        return applicationState;
    }

    public int getActiveSessions()
    {
        return activeSessions;
    }

    public String getName()
    {
        return name;
    }

    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators

    @Override
    public String toString()
    {
        return "Application{" +
               "context='" + context + '\'' +
               ", applicationState=" + applicationState +
               ", activeSessions=" + activeSessions +
               ", name='" + name + '\'' +
               '}';
    }
}
