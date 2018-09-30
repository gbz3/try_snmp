/**
 * 
 */
package com.github.gbz3.try_snmp;

import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.security.USM;

/**
 *
 */
public class V2cStubAgent extends BaseAgent {

	public V2cStubAgent() {
		super( null );
	}

	@Override
	protected void registerManagedObjects() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unregisterManagedObjects() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addUsmUser(USM usm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addNotificationTargets(SnmpTargetMIB targetMIB, SnmpNotificationMIB notificationMIB) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addViews(VacmMIB vacmMIB) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addCommunities(SnmpCommunityMIB communityMIB) {
		// TODO Auto-generated method stub
		
	}

}
