package com.sunlifemalaysia.app4share;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sunlifemalaysia.app4share.controller.web.IpaController;
import com.sunlifemalaysia.app4share.controller.web.QrCodeController;

@SpringBootTest
class App4ShareApplicationTests {

	@Autowired
	private IpaController ipaController;

	@Autowired
	private QrCodeController qrCodeController;

	@Test
	void contextLoads() {

		assertNotNull(ipaController);
		assertNotNull(qrCodeController);
	}

}
