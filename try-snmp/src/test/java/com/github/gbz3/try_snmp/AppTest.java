package com.github.gbz3.try_snmp;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.Inet4Address;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AppTest {

	private V2cStubAgent agent;

	@BeforeEach
	void setUp() throws Exception {
		agent = new V2cStubAgent( "public", "127.0.0.1/161" );
		agent.start();
	}

	@AfterEach
	void tearDown() throws Exception {
		agent.stop();

		// 余分なファイルは削除
		agent.getBootCounterFile().delete();
//		agent.getConfigFile().delete();
	}

	@Test
	@DisplayName( "SNMP-GET - 1件取得" )
    public void testApp() throws Exception
    {
		// expect
		final String MIB_OID_ROOT = ".1.3.6.1.4.1.9999";

		// setup
		agent.regist( Arrays.asList(
				V2cStubAgent.mo( MIB_OID_ROOT + ".1.0", 0 ),
				V2cStubAgent.mo( MIB_OID_ROOT + ".2.0", 1 ),
				V2cStubAgent.mo( MIB_OID_ROOT + ".3.0", 2 ),
				V2cStubAgent.mo( MIB_OID_ROOT + ".4.0", 3 ),
				V2cStubAgent.mo( MIB_OID_ROOT + ".5.0", 4 )
				) );

		// do
		final List<String> result = V2cClient.walk( MIB_OID_ROOT, Inet4Address.getByName( "127.0.0.1" ) );

		// verify
		assertThat( result ).isNotNull();
		assertThat( result.size() ).isGreaterThan( 0 );
		assertThat( result ).containsExactly( "0", "1", "2", "3", "4" );
    }

}
