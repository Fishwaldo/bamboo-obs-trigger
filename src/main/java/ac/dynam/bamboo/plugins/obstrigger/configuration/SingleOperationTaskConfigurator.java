package ac.dynam.bamboo.plugins.obstrigger.configuration;

import com.atlassian.bamboo.security.EncryptionService;
import com.opensymphony.xwork.TextProvider;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Set;

public class SingleOperationTaskConfigurator extends AbstractTomcatConfigurator
{
    private static final Logger log = Logger.getLogger(SingleOperationTaskConfigurator.class);

    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors
    // ----------------------------------------------------------------------------------------------- Interface Methods

    @Override
    protected Set<String> getFieldsToCopy()
    {
        return Collections.emptySet();
    }

    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
