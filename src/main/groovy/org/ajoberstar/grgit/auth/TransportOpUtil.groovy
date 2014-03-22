/*
 * Copyright 2012-2014 the original author or authors.
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

import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.awtui.AwtCredentialsProvider
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

class TransportOpUtil {
	private TransportOpUtil() {
		throw new AssertionError('This class cannot be instantiated.')
	}

	static void configure(TransportCommand cmd, Credentials creds) {
		cmd.transportConfigCallback = new JschAgentProxyConfigCallback()
	}

	private static CredentialsProvider createCredentialsProvider(Credentials creds) {
		if (creds?.username && creds?.password) {
			return new UsernamePasswordCredentialsProvider(creds.username, creds.password)
		}
		return new AwtCredentialsProvider()
	}
}
