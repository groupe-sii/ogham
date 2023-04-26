package oghamcore.ut.core.mimetype

import fr.sii.ogham.core.mimetype.JavaActivationMimeType
import fr.sii.ogham.core.mimetype.MimeType.MimeTypeParameter
import fr.sii.ogham.core.mimetype.ParsedMimeType
import fr.sii.ogham.core.mimetype.RawMimeType
import fr.sii.ogham.testing.extension.common.LogTestInformation
import jakarta.activation.MimeType
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

import static java.util.stream.Collectors.toMap

@Unroll
@LogTestInformation
class MimeTypeSpec extends Specification {
    def "new ParsedMimeType(#mimetype) should result in primaryType='#expectedPrimaryType' subType='#expectedSubType' parameters=#expectedParameters"() {
        when:
        def parsed = new ParsedMimeType(mimetype)

        then:
        parsed.primaryType == expectedPrimaryType
        parsed.subType == expectedSubType
        toMap(parsed.parameters) == expectedParameters

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

    }

    def "new RawMimeType(#mimetype) should result in primaryType='#expectedPrimaryType' subType='#expectedSubType' parameters=#expectedParameters"() {
        when:
        def parsed = new RawMimeType(mimetype)

        then:
        parsed.primaryType == expectedPrimaryType
        parsed.subType == expectedSubType
        toMap(parsed.parameters) == expectedParameters

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

    }

    def "new JavaActivationMimeType(#mimetype) should result in primaryType='#expectedPrimaryType' subType='#expectedSubType' parameters=#expectedParameters"() {
        when:
        def parsed = new JavaActivationMimeType(new MimeType(mimetype))

        then:
        parsed.primaryType == expectedPrimaryType
        parsed.subType == expectedSubType
        toMap(parsed.parameters) == expectedParameters

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

    }

    Map<String, String> toMap(List<MimeTypeParameter> parameters) {
        parameters.stream()
                .collect(toMap(MimeTypeParameter::getName, MimeTypeParameter::getValue))
    }
}
