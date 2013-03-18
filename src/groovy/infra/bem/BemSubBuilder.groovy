package infra.bem

import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.web.util.StreamCharBuffer

/**
 * @author alari
 * @since 3/18/13 5:31 PM
 */
@CompileStatic
class BemSubBuilder extends BemBuilder {

    Map values = [:]

    BemSubBuilder(BemTagLib bem, Writer out) {
        super(bem, out)
    }

    void setProperty(String name, value) {
        StreamCharBuffer buffer
        if (value instanceof Closure) {
            value.delegate = this
            value.resolveStrategy = Closure.DELEGATE_FIRST
        }
        values.put(name, value instanceof Closure ? value.call() : value)
    }
}
