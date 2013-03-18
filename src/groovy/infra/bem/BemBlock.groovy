package infra.bem

import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.web.util.StreamCharBuffer

/**
 * @author alari
 * @since 3/18/13 4:34 PM
 */
@CompileStatic
class BemBlock {
    private static final Closure EMPTY = {}

    String name

    Closure body = EMPTY
    Closure sub

    Map<String,?> subs = [:]

    Map<String,String> modifiers = [:]
    Map model = [:]
    Map<String,String> attrs = [:]

    String getTemplate() {
        "/bem/${name.replace('-', '/')}"
    }

    void setArgs(Map args) {
        if (args.containsKey("_")) {
            model =  (Map)args.remove("_")
        }
        if (args.containsKey("_attrs")) {
            attrs = (Map)args.remove("_attrs")
        }
        if (args.containsKey("_sub")) {
            sub = (Closure)args.remove("_sub")
        }
        modifiers = args
    }
}
