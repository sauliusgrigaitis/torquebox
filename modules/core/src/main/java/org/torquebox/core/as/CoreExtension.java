/*
 * Copyright 2008-2012 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.torquebox.core.as;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIBE;

import java.io.IOException;

import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.OperationEntry;
import org.jboss.logging.Logger;
import org.projectodd.polyglot.core.as.AbstractBootstrappableExtension;
import org.projectodd.polyglot.core.as.GenericSubsystemDescribeHandler;
import org.torquebox.TorqueBox;

public class CoreExtension extends AbstractBootstrappableExtension {

    @Override
    public void initialize(ExtensionContext context) {

        log.info( "Boostrapping TorqueBox Core Subsystem" );
        bootstrap();
        
        log.info( "Initializing TorqueBox Core Subsystem" );
        final SubsystemRegistration registration = context.registerSubsystem( SUBSYSTEM_NAME, 1, 0 );
        final ManagementResourceRegistration subsystem = registration.registerSubsystemModel( CoreSubsystemProviders.SUBSYSTEM );

        subsystem.registerOperationHandler( ADD,
                CoreSubsystemAdd.ADD_INSTANCE,
                CoreSubsystemProviders.SUBSYSTEM_ADD,
                false );
        
        subsystem.registerOperationHandler(DESCRIBE, 
                GenericSubsystemDescribeHandler.INSTANCE, 
                GenericSubsystemDescribeHandler.INSTANCE, 
                false, 
                OperationEntry.EntryType.PRIVATE);


        ManagementResourceRegistration injector = subsystem.registerSubModel( PathElement.pathElement( "injector" ), CoreSubsystemProviders.INJECTOR );

        injector.registerOperationHandler( "add",
                InjectableHandlerAdd.ADD_INSTANCE,
                CoreSubsystemProviders.INJECTOR_ADD,
                false );

        registration.registerXMLElementWriter( CoreSubsystemParser.getInstance() );

        try {
            TorqueBox torquebox = new TorqueBox();
            torquebox.printVersionInfo( log );
            torquebox.verifyJRubyVersion( log );
        } catch (IOException e) {
            log.error( "Failed to load torquebox.properties", e );
        }
    }

    @Override
    public void initializeParsers(ExtensionParsingContext context) {
        context.setSubsystemXmlMapping( SUBSYSTEM_NAME, Namespace.CURRENT.getUriString(), CoreSubsystemParser.getInstance() );
    }

    public static final String SUBSYSTEM_NAME = "torquebox-core";
    static final Logger log = Logger.getLogger( "org.torquebox.core.as" );

}
