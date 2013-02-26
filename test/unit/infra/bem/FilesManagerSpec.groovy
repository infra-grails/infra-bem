package infra.bem

import infra.bem.manager.FilesManager
import spock.lang.Specification

import java.util.regex.Pattern

class FilesManagerSpec extends Specification {

    void "pattern for blocks matcher spec"(boolean res, String token) {
        when:
        def fm = new FilesManager("")
        fm.baseBlock = "root"
        Pattern p = fm.pattern
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
