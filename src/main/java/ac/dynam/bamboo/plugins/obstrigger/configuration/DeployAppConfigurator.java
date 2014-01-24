package ac.dynam.bamboo.plugins.obstrigger.configuration;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.google.common.collect.ImmutableSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public class DeployAppConfigurator extends AbstractOBSConfigurator
{
    private static final Logger log = Logger.getLogger(DeployAppConfigurator.class);
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
