package org.ajoberstar.grgit.auth

import org.eclipse.jgit.api.TransportConfigCallback
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.Transport

/**
 * Callback to configure a Transport to support JSch agent proxying. This
 * allows use of agents, such as ssh-agent and Pageant, to provide SSH
 * authentication.
 * @since 0.1.0
 */
class JschAgentProxyConfigCallback implements TransportConfigCallback {
  private final AuthConfig config

  JschAgentProxyConfigCallback(AuthConfig config) {
    this.config = config
  }

  /**
   * Configures the {@code transport} to support JSch agent proxy,
   * if it is an SSH transport.
   * @param transport the transport to configure
   */
  void configure(Transport transport) {
    if (transport instanceof SshTransport) {
      transport.sshSessionFactory = new JschAgentProxySessionFactory(config)
    }
  }
}
