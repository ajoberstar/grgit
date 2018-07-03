package org.ajoberstar.grgit.auth

import org.eclipse.jgit.api.TransportConfigCallback
import org.eclipse.jgit.transport.JschConfigSessionFactory
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
    // if COMMAND is not enabled, we want the old behavior where we always overrode to use JSch with agent support
    // if COMMAND is enabled, only use Jsch if the external command support didn't detect an available ssh/plink command
    if (transport instanceof SshTransport && (transport.sshSessionFactory instanceof JschConfigSessionFactory || !config.allows(AuthConfig.Option.COMMAND))) {
      transport.sshSessionFactory = new JschAgentProxySessionFactory(config)
    }
  }
}
