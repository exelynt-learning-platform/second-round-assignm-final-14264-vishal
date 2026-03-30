package com.vishal.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "stripe.secret.key=sk_test_dummy_key_for_testing")
class EcommerceApplicationTests {

	@Test
	void contextLoads() {
	}

}
