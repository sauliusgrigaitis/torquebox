/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.torquebox.rails.deployers;

import java.io.IOException;
import java.io.File;
import java.io.Closeable;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VFS;


/**
 * Ensure that log files generated by rails packaged deployments end
 * up somewhere reasonable,
 * e.g. JBOSS_HOME/server/default/log/app.rails, because they can't be
 * written to the deployed archive.
 */
public class RailsLogFileDeployer extends AbstractDeployer {
    
    public RailsLogFileDeployer() {
        setStage(DeploymentStages.PARSE );
    }

    public void deploy(DeploymentUnit unit) throws DeploymentException {
        if ( unit instanceof VFSDeploymentUnit && unit.getName().endsWith( ".rails" )) {
            deploy( (VFSDeploymentUnit) unit );
        }
    }
    
    public void deploy(VFSDeploymentUnit unit) throws DeploymentException {
        VirtualFile logDir = unit.getRoot().getChild( "log" );
        try {
            if ( ! ( logDir.exists() && logDir.getPhysicalFile().canWrite() ) ) {
                File writeableLogDir = new File( System.getProperty( "jboss.server.log.dir" ) + "/" + unit.getSimpleName() );
                writeableLogDir.mkdirs();
                Closeable mount = VFS.mountReal(writeableLogDir, logDir);
                log.warn("Set Rails log directory to "+writeableLogDir.getCanonicalPath());
                unit.addAttachment( Closeable.class, mount );
            }
        } catch (Exception e) {
            throw new DeploymentException( e );
        }
    }

    public void undeploy(DeploymentUnit unit) {
        if ( unit.getName().endsWith( ".rails" )) {
            Closeable mount = unit.getAttachment(Closeable.class);
            if (mount != null) {
                log.info("Closing virtual log directory for "+unit.getSimpleName() );
                try { mount.close(); } catch (IOException ignored) {}
            }
        }
    }

}
