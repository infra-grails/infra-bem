package infra.bem

import groovy.transform.CompileStatic

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
        if (value instanceof Closure) {
            BemBuilder builder = new BemBuilder(bem, new CharArrayWriter())
            value.delegate = builder
            value.resolveStrategy = Closure.DELEGATE_FIRST
            values.put(name, value.call())
        } else values.put(name, value)
    }
}
