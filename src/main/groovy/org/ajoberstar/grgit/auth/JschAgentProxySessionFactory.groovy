/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ajoberstar.grgit.auth

import com.jcraft.jsch.IdentityRepository
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.agentproxy.AgentProxyException
import com.jcraft.jsch.agentproxy.Connector
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository
import com.jcraft.jsch.agentproxy.USocketFactory
import com.jcraft.jsch.agentproxy.connector.PageantConnector
import com.jcraft.jsch.agentproxy.connector.SSHAgentConnector
import com.jcraft.jsch.agentproxy.usocket.JNAUSocketFactory
import com.jcraft.jsch.agentproxy.usocket.NCUSocketFactory

import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig.Host
import org.eclipse.jgit.util.FS

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A session factory that supports use of ssh-agent and Pageant SSH authentication.
 * @since 0.1.0
 */
class JschAgentProxySessionFactory extends JschConfigSessionFactory {
  private static final Logger logger = LoggerFactory.getLogger(JschAgentProxySessionFactory)
  private final AuthConfig config

  JschAgentProxySessionFactory(AuthConfig config) {
    this.config = config
  }

  /**
   * Customize session
   */
  @Override
  protected void configure(Host hc, Session session) {
    config.sessionConfig.each { key, value -> session.setConfig(key, value) }
  }

  /**
   * Obtains a JSch used for creating sessions, with the addition
   * of ssh-agent and Pageant agents, if available.
   * @return the JSch instance
   */
  @Override
  protected JSch getJSch(Host hc, FS fs) throws JSchException {
    JSch jsch
    try {
      jsch = super.getJSch(hc, fs)
    } catch (JSchException e) {
      jsch = super.createDefaultJSch(fs)
    }

    if (config.sshPrivateKeyPath) {
      if (config.getSshPassphrase()) {
        jsch.addIdentity(config.sshPrivateKeyPath, config.getSshPassphrase())
      } else {
        jsch.addIdentity(config.sshPrivateKeyPath)
      }
    }

    Connector con = determineConnector()
    if (con) {
      IdentityRepository remoteRepo = new RemoteIdentityRepository(con)
      if (remoteRepo.identities.empty) {
        logger.info 'not using agent proxy: no identities found'
      } else {
        logger.info 'using agent proxy'
        jsch.setIdentityRepository(remoteRepo)
      }
    } else {
      logger.info 'jsch agent proxy not available'
    }
    return jsch
  }

  /**
   * Chooses which agent proxy connector is used.
   * @return the connector available at this time
   */
  private Connector determineConnector() {
    return [sshAgentSelector, pageantSelector].findResult { selector ->
      selector()
    }
  }

  private final Closure<Connector> sshAgentSelector = {
    try {
      if (!config.allows(AuthConfig.Option.SSHAGENT)) {
        logger.info('ssh-agent option disabled')
        return null
      } else if (SSHAgentConnector.isConnectorAvailable()) {
        USocketFactory usf = determineUSocketFactory()
        if (usf) {
          logger.info 'ssh-agent available'
          return new SSHAgentConnector(usf)
        } else {
          logger.info 'ssh-agent not available'
          return null
        }
      } else {
        logger.info 'ssh-agent not available'
        return null
      }
    } catch (Throwable e) {
      logger.info 'ssh-agent could not be configured: {}', e.message
      logger.debug 'ssh-agent failure details', e
      return null
    }
  }

  private final Closure<Connector> pageantSelector = {
    try {
      if (!config.allows(AuthConfig.Option.PAGEANT)) {
        logger.info('pageant option disabled')
        return null
      } else if (PageantConnector.isConnectorAvailable()) {
        logger.info 'pageant available'
        return new PageantConnector()
      } else {
        logger.info 'pageant not available'
        return null
      }
    } catch (Throwable e) {
      logger.info 'pageant could not be configured: {}', e.message
      logger.debug 'pageant failure details', e
      return null
    }
  }

  /**
   * Choose which socket factory to use.
   * @return a working socket factory or {@code null} if none is
   * available
   */
  private final USocketFactory determineUSocketFactory() {
    return [ncSelector, jnaSelector].findResult { selector ->
      selector()
    }
  }

  private final Closure<USocketFactory> jnaSelector = {
    try {
      return new JNAUSocketFactory()
    } catch (Throwable e) {
      logger.info 'JNA USocketFactory could not be configured: {}', e.message
      logger.debug 'JNA USocketFactory failure details', e
      return null
    }
  }

  private final Closure<USocketFactory> ncSelector = {
    try {
      return new NCUSocketFactory()
    } catch (Throwable e) {
      logger.info 'NetCat USocketFactory could not be configured: {}', e.message
      logger.debug 'NetCat USocketFactory failure details', e
      return null
    }
  }
}
