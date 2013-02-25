package infra.bem

import grails.test.mixin.*
import spock.lang.Specification

import java.util.regex.Pattern

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(BemManagerService)
class BemManagerServiceSpec extends Specification {

    void "pattern for blocks matcher spec"(boolean res, String token) {
        when:
        Pattern p = service.getBlockPattern("root")
        boolean m = token =~ p

        if (!m == res) println "Fail: ${res} for ${token}"

        then:
        m == res

        where:
        res     |   token
        true    |   "b-root"
        true    |   "b-root__element"
        true    |   "m-root_k_v"
        true    |   ".b-root"
        true    |   /"b-root"/
        false   |   "b-root2"
        false   |   /e-root/
        false   |   /-root/
        false   |   "b-root-subblock"
        true    |   /"root"/
        true    |   /root {/
        true    |   / 'root'() {/
        true    |   / "root"(){/

    }
}
