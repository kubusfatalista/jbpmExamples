package org.jbpm.human.resources.tests;

import java.util.List;

public interface OCR {

	List<Long> findBarcodesInDocument(String document);
	Boolean isBarcodeInDB(Long documentNo);
}
