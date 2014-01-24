package ac.dynam.bamboo.plugins.obstrigger.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Provides access to the Tomcat Application Manager of a single Tomcat server.
 */
public interface TomcatApplicationManager
{
    /**
     * Deploys a Tomcat application at the give context path
     * @param contextPath
     * @return result
     * @throws IOException
     */
    @NotNull
    TomcatResult deployApplication(@NotNull String pkg, @NotNull String prj) throws IOException;
}
