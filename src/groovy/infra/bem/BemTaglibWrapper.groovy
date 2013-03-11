package infra.bem

import groovy.transform.CompileStatic

/**
 * @author alari
 * @since 2/5/13 4:26 PM
 */
@CompileStatic
class BemTaglibWrapper {
    private final taglib
    private Writer out

    BemTaglibWrapper(taglib, Writer out) {
        this.taglib = taglib
        this.out = out
    }

    def methodMissing(String name, args) {
        out << taglib.invokeMethod(name, args)
    }
}
