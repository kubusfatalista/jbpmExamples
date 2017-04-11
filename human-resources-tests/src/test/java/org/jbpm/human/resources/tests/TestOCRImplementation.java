package org.jbpm.human.resources.tests;

import java.util.ArrayList;
import java.util.List;

public class TestOCRImplementation implements OCR {

	@Override
	public List<Long> findBarcodesInDocument(String document) {
		System.out.println("Szukam barcodow w dokumencie " + document);
		List<Long> barcodeList = new ArrayList<>();
		barcodeList.add(12345L);
		
		
		return barcodeList;
	}
	
	@Override
	public Boolean isBarcodeInDB(Long documentNo) {
		if(documentNo % 2 == 0)
			return true;
		return false;
	}

}
