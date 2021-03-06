/*

 * Copyright 2009-2013 the original author or authors.
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

package org.cloudfoundry.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudService;

/**
 * Bind Services
 *
 * @author Ali Moghadam
 * @author Scott Frederick
 * @since 1.0.0
 *
 * @goal bind-services
 * @phase process-sources
 */

public class BindServices extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {
		for (CloudService service : getServices()) {
			if (getClient().getService(service.getName()) == null) {
				throw new MojoExecutionException(String.format("Service '%s' does not exist", service.getName()));
			}

			try {
				final CloudApplication application = getClient().getApplication(getAppname());
				if (application.getServices().contains(service.getName())) {
					getLog().info(String.format("Service '%s' is already bound to application '%s'",
							service.getName(), application.getName()));
				} else {
					getClient().bindService(getAppname(), service.getName());
					getLog().info(String.format("Binding Service '%s'", service.getName()));
				}
			}
			catch (CloudFoundryException e) {
				throw new MojoExecutionException(String.format("Application '%s' does not exist", getAppname()));
			}
		}
	}
}