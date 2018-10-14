/**
 * 
 */
package com.github.gbz3.try_snmp;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.snmp4j.TransportMapping;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.transport.TransportMappings;

/**
 *
 * @see <a href="https://examples.javacodegeeks.com/enterprise-java/snmp4j/test-driven-development-snmp4j/" >Test-Driven Development with SNMP4J</a>
 */
public class V2cStubAgent extends BaseAgent {

	// Variable に代入可能な型は https://www.snmp4j.org/doc/org/snmp4j/smi/Variable.html 参照
	/** オクテット文字列のオブジェクトを作成 */
	public static MOScalar<? extends Variable> mo( String oid, String value ) {
		return new MOScalar<OctetString>( new OID( oid ), MOAccessImpl.ACCESS_READ_ONLY, new OctetString( value ) );
	}

	/** 整数のオブジェクトを作成 */
	public static MOScalar<? extends Variable> mo( String oid, int value ) {
		return new MOScalar<Integer32>( new OID( oid ), MOAccessImpl.ACCESS_READ_ONLY, new Integer32( value ) );
	}

	/** IPアドレスのオブジェクトを作成 */
	public static MOScalar<? extends Variable> mo( String oid, InetAddress addr ) {
		return new MOScalar<IpAddress>( new OID( oid ), MOAccessImpl.ACCESS_READ_ONLY, new IpAddress( addr ) );
	}

	/** オブジェクトを登録。同一OIDが登録済みの場合は失敗する。 */
	public void regist( final List<ManagedObject> mol ) {
		try {
			for( ManagedObject mo: mol ) {
				server.register( mo, null );
			}

			server.getRegistry().values().stream().forEach( s -> System.out.println( communityName + " ManagedObject=" + s.toString() ) );
		} catch ( DuplicateRegistrationException e ) {
			e.printStackTrace();
		}
	}

	private final String communityName;
	private final String address;

	public V2cStubAgent( final String cn, final String p ) {
		super( new File( "bootCounterFile.txt" ), new File( "configFile.txt" ),
				new CommandProcessor( new OctetString( MPv3.createLocalEngineID() ) ) );
		communityName = cn;
		address = p;
	}

	@Override
	protected void initTransportMappings() throws IOException {
		transportMappings = new TransportMapping<?>[] { TransportMappings.getInstance().createTransportMapping( GenericAddress.parse( address ) ) };
	}

	public void start() throws IOException {
		init();
		// addShutdownHook();
		getServer().addContext( new OctetString( "public" ) );
		finishInit();
		run();
		sendColdStartNotification();
	}

	@Override
	protected void registerManagedObjects() {
		getSnmpv2MIB().unregisterMOs( server, getContext( getSnmpv2MIB() ) );
	}

	@Override
	protected void unregisterManagedObjects() {
		// do nothing
	}

	@Override
	protected void addUsmUser(USM usm) {
		// do nothing
	}

	@Override
	protected void addNotificationTargets(SnmpTargetMIB targetMIB, SnmpNotificationMIB notificationMIB) {
		// do nothing
	}

	@Override
	protected void addViews(VacmMIB vacmMIB) {
		vacmMIB.addGroup( SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString( "cpublic" ), new OctetString( "v1v2group" ), StorageType.nonVolatile );
		vacmMIB.addAccess( new OctetString( "v1v2group" ), new OctetString( "public" ), SecurityModel.SECURITY_MODEL_ANY, SecurityLevel.NOAUTH_NOPRIV,
				MutableVACM.VACM_MATCH_EXACT, new OctetString( "fullReadView" ), new OctetString( "fullWriteView" ), new OctetString( "fullNotifyView" ), StorageType.nonVolatile );
		vacmMIB.addViewTreeFamily( new OctetString( "fullReadView" ), new OID( ".1.3" ), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile );
	}

	@Override
	protected void addCommunities(SnmpCommunityMIB communityMIB) {
		Variable[] com2sec = new Variable[] {
			new OctetString( communityName ),				// community name	
			new OctetString( "cpublic" ),							// security name
			getAgent().getContextEngineID(),					// local engine ID
			new OctetString( "public" ),							// default context name
			new OctetString(),											// transport tag
			new Integer32( StorageType.nonVolatile ),	// storage type
			new Integer32( RowStatus.active )				// row status
		};
		communityMIB.getSnmpCommunityEntry().addRow(
				communityMIB.getSnmpCommunityEntry().createRow( new OctetString( "public2public" ).toSubIndex( true ), com2sec )
				);
	}

}
