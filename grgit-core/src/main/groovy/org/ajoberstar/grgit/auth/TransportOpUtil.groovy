package org.ajoberstar.grgit.auth

import org.ajoberstar.grgit.Credentials

import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.errors.UnsupportedCredentialItem
import org.eclipse.jgit.transport.CredentialItem
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Utility class that allows a JGit {@code TransportCommand} to be configured
 * to use additional authentication options.
 */
final class TransportOpUtil {
  private static final Logger logger = LoggerFactory.getLogger(TransportOpUtil)

  private TransportOpUtil() {
    throw new AssertionError('This class cannot be instantiated.')
  }

  /**
   * Configures the given transport command with the given credentials.
   * @param cmd the command to configure
   * @param creds the hardcoded credentials to use, if not {@code null}
   */
  static void configure(TransportCommand cmd, Credentials creds) {
    AuthConfig config = AuthConfig.fromSystem()
    cmd.credentialsProvider = determineCredentialsProvider(config, creds)
  }

  private static CredentialsProvider determineCredentialsProvider(AuthConfig config, Credentials creds) {
    Credentials systemCreds = config.hardcodedCreds
    if (creds?.populated) {
      logger.info('using hardcoded credentials provided programmatically')
      return new UsernamePasswordCredentialsProvider(creds.username, creds.password)
    } else if (systemCreds?.populated) {
      logger.info('using hardcoded credentials from system properties')
      return new UsernamePasswordCredentialsProvider(systemCreds.username, systemCreds.password)
    } else {
      return null
    }
  }
}
