package ac.dynam.bamboo.plugins.obstrigger.manager;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class TomcatResult
{
    private static final Logger log = Logger.getLogger(TomcatResult.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties

    private final boolean successful;
    private final String reason;

    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors

    public TomcatResult(final boolean successful, final String reason)
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
    public static TomcatResult parse(@NotNull String line)
    {
        if (line.startsWith("OK"))
        {
            return new TomcatResult(true, line);
        }
        else
        {
            return new TomcatResult(false, line);
        }
    }

    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
