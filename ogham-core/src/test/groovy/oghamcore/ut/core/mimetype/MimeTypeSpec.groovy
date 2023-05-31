package oghamcore.ut.core.mimetype

import fr.sii.ogham.core.mimetype.JavaActivationMimeType
import fr.sii.ogham.core.mimetype.MimeType.MimeTypeParameter
import fr.sii.ogham.core.mimetype.ParsedMimeType
import fr.sii.ogham.core.mimetype.RawMimeType
import fr.sii.ogham.testing.extension.common.LogTestInformation
import jakarta.activation.MimeType
import spock.lang.Specification
import spock.lang.Unroll

import static java.util.stream.Collectors.toMap

@Unroll
@LogTestInformation
class MimeTypeSpec extends Specification {
    def "new ParsedMimeType(#mimetype) should result in baseType=#expectedBaseType primaryType='#expectedPrimaryType' subType='#expectedSubType' parameters=#expectedParameters"() {
        when:
        def parsed = new ParsedMimeType(mimetype)

        then:
        parsed.baseType == expectedBaseType
        parsed.primaryType == expectedPrimaryType
        parsed.subType == expectedSubType
        toMap(parsed.parameters) == expectedParameters
        toMapUsingGetParameter(parsed) == expectedParameters

        where:
        mimetype                                                       || expectedPrimaryType | expectedSubType    | expectedParameters
        "text/plain"                                                   || "text"              | "plain"            | [:]
        "text/html"                                                    || "text"              | "html"             | [:]
        "image/png"                                                    || "image"             | "png"              | [:]
        "image/jpeg"                                                   || "image"             | "jpeg"             | [:]
        "image/svg+xml"                                                || "image"             | "svg+xml"          | [:]
        "application/json"                                             || "application"       | "json"             | [:]
        "application/octet-stream"                                     || "application"       | "octet-stream"     | [:]
        "application/x-rar-compressed"                                 || "application"       | "x-rar-compressed" | [:]
        "application/vnd.mspowerpoint"                                 || "application"       | "vnd.mspowerpoint" | [:]
        "application/problem+json"                                     || "application"       | "problem+json"     | [:]
        "multipart/form-data"                                          || "multipart"         | "form-data"        | [:]
        // with parameters
        "text/plain;charset=utf-8"                                     || "text"              | "plain"            | [charset: "utf-8"]
        "multipart/form-data; boundary=--------------8721656041911418" || "multipart"         | "form-data"        | [boundary: "--------------8721656041911418"]
        "multipart/form-data; charset=utf-8; boundary=-----1234"       || "multipart"         | "form-data"        | [charset: "utf-8", boundary: "-----1234"]

        expectedBaseType = "${expectedPrimaryType}/${expectedSubType}"
    }

    def "new ParsedMimeType(#mimetype) should #matchDesc #comparedMimetype"() {
        when:
        def parsed = new ParsedMimeType(mimetype)
        def other = new ParsedMimeType(comparedMimetype)

        then:
        parsed.matches(other) == shouldMatch

        where:
        mimetype                   | comparedMimetype           || shouldMatch
        "text/plain"               | "text/plain"               || true
        "text/plain"               | "text/html"                || false
        "text/plain"               | "text/plain;charset=utf-8" || true
        "text/plain"               | "text/html;charset=utf-8"  || false
        "text/html"                | "text/plain"               || false
        "text/html"                | "text/html"                || true
        "text/html"                | "text/plain;charset=utf-8" || false
        "text/html"                | "text/html;charset=utf-8"  || true
        "application/json"         | "application/json"         || true
        "application/json"         | "application/problem+json" || false
        "application/problem+json" | "application/json"         || false
        "application/problem+json" | "application/problem+json" || true
        "application/json+problem" | "application/json"         || false
        "application/json+problem" | "application/problem+json" || false
        "application/*"            | "text/plain"               || false
        "application/*"            | "application/json"         || true
        "application/*"            | "application/problem+json" || true

        matchDesc = shouldMatch ? "match" : "not match"
    }


    def "new RawMimeType(#mimetype) should result in baseType=#expectedBaseType primaryType='#expectedPrimaryType' subType='#expectedSubType' parameters=#expectedParameters"() {
        when:
        def parsed = new RawMimeType(mimetype)

        then:
        parsed.baseType == expectedBaseType
        parsed.primaryType == expectedPrimaryType
        parsed.subType == expectedSubType
        toMap(parsed.parameters) == expectedParameters
        toMapUsingGetParameter(parsed) == expectedParameters

        where:
        mimetype                                                       || expectedPrimaryType | expectedSubType    | expectedParameters
        "text/plain"                                                   || "text"              | "plain"            | [:]
        "text/html"                                                    || "text"              | "html"             | [:]
        "image/png"                                                    || "image"             | "png"              | [:]
        "image/jpeg"                                                   || "image"             | "jpeg"             | [:]
        "image/svg+xml"                                                || "image"             | "svg+xml"          | [:]
        "application/json"                                             || "application"       | "json"             | [:]
        "application/octet-stream"                                     || "application"       | "octet-stream"     | [:]
        "application/x-rar-compressed"                                 || "application"       | "x-rar-compressed" | [:]
        "application/vnd.mspowerpoint"                                 || "application"       | "vnd.mspowerpoint" | [:]
        "application/problem+json"                                     || "application"       | "problem+json"     | [:]
        "multipart/form-data"                                          || "multipart"         | "form-data"        | [:]
        // with parameters
        "text/plain;charset=utf-8"                                     || "text"              | "plain"            | [charset: "utf-8"]
        "multipart/form-data; boundary=--------------8721656041911418" || "multipart"         | "form-data"        | [boundary: "--------------8721656041911418"]
        "multipart/form-data; charset=utf-8; boundary=-----1234"       || "multipart"         | "form-data"        | [charset: "utf-8", boundary: "-----1234"]

        expectedBaseType = "${expectedPrimaryType}/${expectedSubType}"
    }

    def "new RawMimeType(#mimetype) should #matchDesc #comparedMimetype"() {
        when:
        def parsed = new RawMimeType(mimetype)
        def other = new RawMimeType(comparedMimetype)

        then:
        parsed.matches(other) == shouldMatch

        where:
        mimetype                   | comparedMimetype           || shouldMatch
        "text/plain"               | "text/plain"               || true
        "text/plain"               | "text/html"                || false
        "text/plain"               | "text/plain;charset=utf-8" || true
        "text/plain"               | "text/html;charset=utf-8"  || false
        "text/html"                | "text/plain"               || false
        "text/html"                | "text/html"                || true
        "text/html"                | "text/plain;charset=utf-8" || false
        "text/html"                | "text/html;charset=utf-8"  || true
        "application/json"         | "application/json"         || true
        "application/json"         | "application/problem+json" || false
        "application/problem+json" | "application/json"         || false
        "application/problem+json" | "application/problem+json" || true
        "application/json+problem" | "application/json"         || false
        "application/json+problem" | "application/problem+json" || false
        "application/*"            | "text/plain"               || false
        "application/*"            | "application/json"         || true
        "application/*"            | "application/problem+json" || true

        matchDesc = shouldMatch ? "match" : "not match"
    }


    def "new JavaActivationMimeType(#mimetype) should result in baseType=#expectedBaseType primaryType='#expectedPrimaryType' subType='#expectedSubType' parameters=#expectedParameters"() {
        when:
        def parsed = new JavaActivationMimeType(new MimeType(mimetype))

        then:
        parsed.baseType == expectedBaseType
        parsed.primaryType == expectedPrimaryType
        parsed.subType == expectedSubType
        toMap(parsed.parameters) == expectedParameters
        toMapUsingGetParameter(parsed) == expectedParameters

        where:
        mimetype                                                       || expectedPrimaryType | expectedSubType    | expectedParameters
        "text/plain"                                                   || "text"              | "plain"            | [:]
        "text/html"                                                    || "text"              | "html"             | [:]
        "image/png"                                                    || "image"             | "png"              | [:]
        "image/jpeg"                                                   || "image"             | "jpeg"             | [:]
        "image/svg+xml"                                                || "image"             | "svg+xml"          | [:]
        "application/json"                                             || "application"       | "json"             | [:]
        "application/octet-stream"                                     || "application"       | "octet-stream"     | [:]
        "application/x-rar-compressed"                                 || "application"       | "x-rar-compressed" | [:]
        "application/vnd.mspowerpoint"                                 || "application"       | "vnd.mspowerpoint" | [:]
        "application/problem+json"                                     || "application"       | "problem+json"     | [:]
        "multipart/form-data"                                          || "multipart"         | "form-data"        | [:]
        // with parameters
        "text/plain;charset=utf-8"                                     || "text"              | "plain"            | [charset: "utf-8"]
        "multipart/form-data; boundary=--------------8721656041911418" || "multipart"         | "form-data"        | [boundary: "--------------8721656041911418"]
        "multipart/form-data; charset=utf-8; boundary=-----1234"       || "multipart"         | "form-data"        | [charset: "utf-8", boundary: "-----1234"]

        expectedBaseType = "${expectedPrimaryType}/${expectedSubType}"
    }

    def "new JavaActivationMimeType(#mimetype) should #matchDesc #comparedMimetype"() {
        when:
        def parsed = JavaActivationMimeType.fromString(mimetype)
        def other = JavaActivationMimeType.fromString(comparedMimetype)

        then:
        parsed.matches(other) == shouldMatch

        where:
        mimetype                   | comparedMimetype           || shouldMatch
        "text/plain"               | "text/plain"               || true
        "text/plain"               | "text/html"                || false
        "text/plain"               | "text/plain;charset=utf-8" || true
        "text/plain"               | "text/html;charset=utf-8"  || false
        "text/html"                | "text/plain"               || false
        "text/html"                | "text/html"                || true
        "text/html"                | "text/plain;charset=utf-8" || false
        "text/html"                | "text/html;charset=utf-8"  || true
        "application/json"         | "application/json"         || true
        "application/json"         | "application/problem+json" || false
        "application/problem+json" | "application/json"         || false
        "application/problem+json" | "application/problem+json" || true
        "application/json+problem" | "application/json"         || false
        "application/json+problem" | "application/problem+json" || false
        "application/*"            | "text/plain"               || false
        "application/*"            | "application/json"         || true
        "application/*"            | "application/problem+json" || true

        matchDesc = shouldMatch ? "match" : "not match"
    }



    Map<String, String> toMap(List<MimeTypeParameter> parameters) {
        parameters.stream()
                .collect(toMap(MimeTypeParameter::getName, MimeTypeParameter::getValue))
    }

    Map<String, String> toMapUsingGetParameter(fr.sii.ogham.core.mimetype.MimeType mimetype) {
        mimetype.parameters.stream()
                .collect(toMap(MimeTypeParameter::getName, p -> mimetype.getParameter(p.getName())))
    }
}
