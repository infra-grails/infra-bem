package infra.bem.manager

import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

/**
 * @author alari
 * @since 2/25/13 6:57 PM
 */
class FilesManager {
    static protected final STATIC_EXTENSIONS = ["css", "less", "sass", "scss", "styl", "coffee", "js", "html"]
    static protected final VIEWS_EXTENSIONS = ["gsp"]

    protected final String basedir
    protected Pattern pattern
    protected String baseBlock

    FilesManager(String basedir) {
        this.basedir = basedir
    }

    void setBaseBlock(String blockName) {
        baseBlock = blockName
        pattern = ~/([bm]-|["'\s\{;]|^)(${blockName})([_"'\s\(\{]|$)/
    }

    Pattern getPattern() {
        pattern
    }

    LookupFiles lookup(Collection<String> extensions) {
        if (!baseBlock) {
            throw new IllegalStateException("You have to specify Base Block to use lookups!")
        }
        new LookupFiles().lookup(extensions, baseBlock)
    }

    Path getViews() {
        Paths.get("${basedir}/grails-app/views")
    }

    Path getStatics() {
        Paths.get("${basedir}/web-app")
    }

    Path getConfPath() {
        Paths.get("${basedir}/grails-app/conf")
    }
}
