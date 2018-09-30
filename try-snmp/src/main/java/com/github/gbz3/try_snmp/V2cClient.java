package com.github.gbz3.try_snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class V2cClient {

    public static void main( String[] args ) throws Exception {
    	final TransportMapping tm = new DefaultUdpTransportMapping();
    	final Snmp snmp = new Snmp( tm );
    	snmp.listen();
    	
    	final PDU pdu = new PDU();
    	final OID targetOID = new OID( ".1.3.6.1.2.1.4.21.1.1.0.0.0.0" );
    	pdu.add( new VariableBinding( targetOID ) );
    	pdu.setType( PDU.GETNEXT );
    	
    	CommunityTarget target = new CommunityTarget();
    	target.setCommunity( new OctetString( "public" ) );
    	target.setAddress( GenericAddress.parse( "udp:127.0.0.1/161" ) );
    	target.setRetries( 0 );
    	target.setTimeout( 1000 );
    	target.setVersion( SnmpConstants.version2c );
    	
    	final ResponseEvent event = snmp.send( pdu, target );
    	if( event != null ) {
//    		System.out.println( String.format( "response=[%1$s]", event.getResponse().get( 0 ).getVariable() ) );
    		System.out.println( String.format( "response=[%1$s]", event.getResponse() ) );
    		final OID resultOid = event.getResponse().get( 0 ).getOid();
    		System.out.println( String.format( "oid=[%1$s][%2$d] value=[%3$s]", resultOid, targetOID.leftMostCompare( targetOID.size(), resultOid ), event.getResponse().get( 0 ).getVariable().toString() ) );
    	} else {
    		System.out.println( "response is null." );
    	}
    }

}
