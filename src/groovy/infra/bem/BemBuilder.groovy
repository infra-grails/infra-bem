package infra.bem

import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.web.util.StreamCharBuffer

/**
 * @author alari
 * @since 2/5/13 10:45 AM
 */
@CompileStatic
class BemBuilder {
    private final BemTagLib bem
    private final Writer out

    private final Map<String,BemTaglibWrapper> taglibsByPrefix = [:]

    BemBuilder(BemTagLib bem, Writer out) {
        this.bem = bem
        this.out = out
    }

    def methodMissing(String name, args) {
        BemBlock block = new BemBlock(name: name)

        Object[] argsList = args as Object[]

        if (args) {

            if (argsList?.first() instanceof Map) {
                block.args = (Map)argsList.first()

            }

            if (argsList?.last() instanceof Closure) {
                Closure body = (Closure)argsList.last()
                body.delegate = this
                body.resolveStrategy = Closure.DELEGATE_FIRST
                block.body = body
            }
        }

        if (block.sub) {
            Writer subOut = new CharArrayWriter()
            BemSubBuilder subBuilder = new BemSubBuilder(bem, subOut)
            block.sub.delegate = subBuilder
            block.sub.resolveStrategy = Closure.DELEGATE_FIRST
            block.sub.call()
            block.subs = subBuilder.values
        }

        out << bem.buildBlock(block)
    }

    def propertyMissing(String name) {
        // Underscored taglibs works like default ones -- to use them in attributes
        if (name.startsWith("_")) {
            return bem.propertyMissing(name.substring(1))
        }
        // Taglibs are wrapped to write to output instead of returning
        if (!taglibsByPrefix.containsKey(name)) {
            taglibsByPrefix.put(name, new BemTaglibWrapper(bem.propertyMissing(name), out))
        }
        taglibsByPrefix.get(name)
    }
}
