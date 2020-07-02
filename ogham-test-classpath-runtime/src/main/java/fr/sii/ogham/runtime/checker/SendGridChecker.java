package fr.sii.ogham.runtime.checker;

import java.io.IOException;

public interface SendGridChecker {

	void assertEmailWithoutTemplate() throws IOException;

	void assertEmailWithThymeleaf() throws IOException;

	void assertEmailWithFreemarker() throws IOException;

	void assertEmailWithThymeleafAndFreemarker() throws IOException;

}