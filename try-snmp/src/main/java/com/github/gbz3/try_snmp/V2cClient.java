package com.github.gbz3.try_snmp;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class V2cClient {

	public static List<String> walk( final String baseOid, final InetAddress addr ) throws IOException {
		final TransportMapping<UdpAddress> tm = new DefaultUdpTransportMapping();
    	final Snmp snmp = new Snmp( tm );
    	snmp.listen();
		
    	final PDU pdu = new PDU();
    	final OID baseOID = new OID( baseOid );
    	pdu.add( new VariableBinding( baseOID ) );
    	pdu.setType( PDU.GETNEXT );

    	CommunityTarget target = new CommunityTarget();
    	target.setCommunity( new OctetString( "public" ) );
    	target.setAddress( GenericAddress.parse( "udp:" + addr.getHostAddress() + "/161" ) );
    	target.setRetries( 0 );
    	target.setTimeout( 1000L );
    	target.setVersion( SnmpConstants.version2c );

    	final List<String> result = new ArrayList<String>();
    	while( true ) {
        	final ResponseEvent event = snmp.send( pdu, target );
        	if( event.getResponse() == null ) {
        		System.out.println( "timed out. request=" + event.getRequest() );
        	}
        	final VariableBinding vb = event.getResponse().get( 0 );
        	if( baseOID.leftMostCompare( baseOID.size(), vb.getOid() ) < 0 ) {
        		System.out.println( "baseOID not matched. oid=" + vb.getOid() );
        		break;
        	}
        	System.out.println( "result=[" + vb.toString() + "]" );
    		result.add( vb.toValueString() );

    		pdu.clear();
    		pdu.add( new VariableBinding( new OID( vb.getOid() ) ) );
    		pdu.setType( PDU.GETNEXT );
    	}

    	return result.size() > 0? result: null;
	}

	public static void main( String[] args ) throws Exception {
		System.out.println( "result=" + walk( ".1.3.6.1.2.1.4.21.1.1", Inet4Address.getByName( "127.0.0.1" ) ) );

//    	final TransportMapping<UdpAddress> tm = new DefaultUdpTransportMapping();
//    	final Snmp snmp = new Snmp( tm );
//    	snmp.listen();
//    	
//    	final PDU pdu = new PDU();
//    	final OID targetOID = new OID( ".1.3.6.1.2.1.4.21.1.1" );
//    	pdu.add( new VariableBinding( targetOID ) );
//    	pdu.setType( PDU.GETNEXT );
//    	
//    	CommunityTarget target = new CommunityTarget();
//    	target.setCommunity( new OctetString( "public" ) );
//    	target.setAddress( GenericAddress.parse( "udp:127.0.0.1/161" ) );
//    	target.setRetries( 0 );
//    	target.setTimeout( 1000 );
//    	target.setVersion( SnmpConstants.version2c );
//    	
//    	final ResponseEvent event = snmp.send( pdu, target );
//    	if( event != null ) {
////    		System.out.println( String.format( "response=[%1$s]", event.getResponse().get( 0 ).getVariable() ) );
//    		System.out.println( String.format( "response=[%1$s]", event.getResponse() ) );
//    		final OID resultOid = event.getResponse().get( 0 ).getOid();
//    		System.out.println( String.format( "oid=[%1$s][%2$d] value=[%3$s]", resultOid, targetOID.leftMostCompare( targetOID.size(), resultOid ), event.getResponse().get( 0 ).getVariable().toString() ) );
//    	} else {
//    		System.out.println( "response is null." );
//    	}
    }

}
